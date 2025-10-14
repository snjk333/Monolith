package com.oleksandr.monolith.Coordinator;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDetailsDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Booking.util.BookingMapper;
import com.oleksandr.monolith.Booking.Service.BookingService;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import com.oleksandr.monolith.Ticket.EntityRepo.TICKET_STATUS;
import com.oleksandr.monolith.Ticket.Service.TicketService;
import com.oleksandr.monolith.Ticket.util.TicketMapper;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.Service.UserService;
import com.oleksandr.monolith.User.util.UserMapper;
import com.oleksandr.monolith.common.exceptions.BookingAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingCoordinator {

    private final UserService userService;
    private final TicketService ticketService;
    private final BookingService bookingService;

    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    public BookingCoordinator(UserService userService, TicketService ticketService, BookingService bookingService, BookingMapper bookingMapper, UserMapper userMapper, TicketMapper ticketMapper) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.userMapper = userMapper;
        this.ticketMapper = ticketMapper;
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
            throw new BookingAccessDeniedException("You can't cancel sold ticket");
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
}