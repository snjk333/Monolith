package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.User;
import com.oleksandr.monolith.entity.enums.BOOKING_STATUS;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.repository.BookingRepository;
import com.oleksandr.monolith.service.interfaces.UserService;
import com.oleksandr.monolith.service.interfaces.BookingService;
import com.oleksandr.monolith.service.interfaces.TicketService;
import com.oleksandr.monolith.util.BookingMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final TicketService ticketService;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository, TicketService ticketService, BookingMapper bookingMapper, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
    }

    @Transactional
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        User user = userService.getOrCreateUser(userId);
        Ticket ticket = ticketService.findEntityById(ticketId); // должен throw если нет

        if (ticket.getStatus() != TICKET_STATUS.AVAILABLE) {
            // пока просто возвращаем null или throw
        }

        ticketService.updateStatus(ticketId, TICKET_STATUS.RESERVED);

        Booking booking = new Booking();
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BOOKING_STATUS.CREATED);
        booking.setUser(user);
        booking.setTicket(ticket);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.mapToDto(saved);
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingDTO> getBookingsByUser(UUID userId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        return bookingMapper.mapEntityListToDtoList(bookings);
    }
    @Transactional
    @Override
    public BookingDTO cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);//todo exception
        booking.setStatus(BOOKING_STATUS.CANCELLED);
        ticketService.updateStatus(booking.getTicket().getId(), TICKET_STATUS.AVAILABLE);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }
    @Transactional
    @Override
    public BookingDTO completeBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);//todo exception
        booking.setStatus(BOOKING_STATUS.PAID);
        ticketService.updateStatus(booking.getTicket().getId(), TICKET_STATUS.SOLD);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isTicketAvailable(UUID ticketId) {
        Ticket ticket = ticketService.findEntityById(ticketId); //todo exception
        return ticket.getStatus() == TICKET_STATUS.AVAILABLE;
    }
}
