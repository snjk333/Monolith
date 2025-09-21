package com.oleksandr.monolith.Coordinator;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.util.BookingMapper;
import com.oleksandr.monolith.Booking.Service.BookingService;
import com.oleksandr.monolith.Ticket.Service.TicketService;
import com.oleksandr.monolith.User.Service.UserService;
import com.oleksandr.monolith.common.exceptions.BookingAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookingCoordinator {

    private final UserService userService;
    private final TicketService ticketService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingCoordinator(UserService userService,
                              TicketService ticketService,
                              BookingService bookingService,
                              BookingMapper bookingMapper) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        var user = userService.getOrCreateUser(userId);
        var ticket = ticketService.reserveTicket(ticketId);
        var booking = bookingService.createBooking(user, ticket);

        // Маппинг DTO здесь, на границе внутреннего и внешнего
        return bookingMapper.mapToDto(booking);
    }

    @Transactional
    public BookingDTO cancelBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markAvailable(booking.getTicket());
        var cancelled = bookingService.cancelBooking(booking);

        return bookingMapper.mapToDto(cancelled);
    }

    @Transactional
    public BookingDTO completeBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markSold(booking.getTicket());
        var completed = bookingService.completeBooking(booking);

        return bookingMapper.mapToDto(completed);
    }
}