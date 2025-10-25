package com.oleksandr.monolith.Coordinator;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDetailsDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Booking.util.BookingMapper;
import com.oleksandr.monolith.Booking.Service.BookingService;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import com.oleksandr.monolith.Ticket.EntityRepo.TICKET_STATUS;
import com.oleksandr.monolith.Ticket.Service.TicketService;
import com.oleksandr.monolith.Ticket.util.TicketMapper;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.EntityRepo.User;
import com.oleksandr.monolith.User.Service.UserService;
import com.oleksandr.monolith.User.util.UserMapper;
import com.oleksandr.monolith.common.exceptions.BookingAccessDeniedException;
import com.oleksandr.monolith.integration.payU.PayUClient;
import com.oleksandr.monolith.integration.payU.dto.PayUAuthResponseDTO;
import com.oleksandr.monolith.integration.payU.dto.PayUOrderRequestDTO;
import com.oleksandr.monolith.integration.payU.dto.PayUOrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BookingCoordinator {

    private final UserService userService;
    private final TicketService ticketService;
    private final BookingService bookingService;

    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    private final PayUClient payUClient;

    @Value("${payu.notify.base.url}")
    private String notifyBaseUrl;

    @Value("${app.frontend.url}")
    private String frontendUrlFromProperties;

    private static final String PAYU_NOTIFICATION_PATH = "/monolith/api/payu/notifications";

    public BookingCoordinator(UserService userService, TicketService ticketService, BookingService bookingService, BookingMapper bookingMapper, UserMapper userMapper, TicketMapper ticketMapper, PayUClient payUClient) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.userMapper = userMapper;
        this.ticketMapper = ticketMapper;
        this.payUClient = payUClient;
    }

    @Transactional
    public BookingSummaryDTO createBooking(UUID userId, UUID ticketId) {
        var user = userService.getOrCreateUser(userId);
        var ticket = ticketService.reserveTicket(ticketId);
        var booking = bookingService.createBooking(user, ticket);

        return bookingMapper.mapToSummaryDto(booking);
    }

    @Transactional
    public BookingSummaryDTO cancelBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if(booking.getTicket().getStatus().equals(TICKET_STATUS.SOLD)){
            throw new BookingAccessDeniedException("You can't cancel booking with sold ticket");
        }

        if(booking.getStatus().equals(BOOKING_STATUS.WAITING_FOR_PAYMENT)){
            throw new BookingAccessDeniedException("You can't booking ticket while payment");
        }

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markAvailable(booking.getTicket());
        var cancelled = bookingService.cancelBooking(booking);

        return bookingMapper.mapToSummaryDto(cancelled);
    }


    @Transactional
    public BookingSummaryDTO completeBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markSold(booking.getTicket());
        var completed = bookingService.completeBooking(booking);

        return bookingMapper.mapToSummaryDto(completed);
    }

    public BookingDetailsDTO getBookingDetails(UUID id) {
        var booking = bookingService.findById(id);
        var user = userService.getOrCreateUser(booking.getUser().getId());
        var ticket = booking.getTicket();

        UserSummaryDTO userSummaryDTO = userMapper.mapToSummaryDto(user);
        TicketDTO ticketDTO = ticketMapper.mapToDto(ticket);

        return BookingDetailsDTO
                .builder()
                .id(booking.getId())
                .user(userSummaryDTO)
                .ticket(ticketDTO)
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .version(booking.getVersion())
                .build();
    }


    public List<BookingSummaryDTO> getUserBookings(UUID userID) {
        var user = userService.getOrCreateUser(userID);
        List<Booking> bookingsList = user.getBookings();

        return bookingMapper.mapListToSummaryListDto(bookingsList);
    }

    @Transactional
    public String initiatePayment(UUID bookingId, UUID userId, String customerIp) {
        log.info("Initiating payment for booking: {} by user: {}", bookingId, userId);

        Booking booking = bookingService.findById(bookingId);
        User user = booking.getUser();
        var ticket = booking.getTicket();

        if (!user.getId().equals(userId)) {
            log.warn("Access denied for user {} trying to pay for booking {}", userId, bookingId);
            throw new BookingAccessDeniedException("User is not authorized to pay for this booking");
        }

        booking.setStatus(BOOKING_STATUS.WAITING_FOR_PAYMENT);
        PayUAuthResponseDTO authToken = payUClient.getAccessToken();
        log.info("Successfully got PayU token");

        String totalAmount = String.valueOf(((int) (ticket.getPrice()*100)));

        PayUOrderRequestDTO.Buyer buyerDto = PayUOrderRequestDTO.Buyer.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName() != null ? user.getFirstName() : user.getUsername())
                .lastName(user.getLastName() != null ? user.getLastName() : "User")
                .phone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "123456789")
                .language("pl")
                .build();

        PayUOrderRequestDTO.Product productDto = PayUOrderRequestDTO.Product.builder()
                .name("Ticket to: " + ticket.getEvent().getName())
                .unitPrice(totalAmount)
                .quantity("1")
                .build();

        String fullNotifyUrl;

        if (notifyBaseUrl.contains("webhook.site")) {
            fullNotifyUrl = notifyBaseUrl;
            log.info("Using webhook.site for testing notifications: {}", fullNotifyUrl);
        } else {
            fullNotifyUrl = notifyBaseUrl + PAYU_NOTIFICATION_PATH;
            log.info("Using custom webhook URL: {}", fullNotifyUrl);
        }
        
        PayUOrderRequestDTO orderRequest = PayUOrderRequestDTO.builder()
                .customerIp(customerIp)
                .extOrderId(booking.getId().toString())
                .description("Ticket reservation: " + ticket.getEvent().getName())
                .currencyCode("PLN")
                .totalAmount(totalAmount)
                .buyer(buyerDto)
                .products(List.of(productDto))
                .notifyUrl(fullNotifyUrl)
                .continueUrl(frontendUrlFromProperties + "/payment/success")
                .build();

        PayUOrderResponseDTO orderResponse = payUClient.createOrder(orderRequest, authToken.getAccessToken());
        log.info("PayU order created with ID: {}", orderResponse.getOrderId());

        return orderResponse.getRedirectUri();
    }
}