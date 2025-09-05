package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.User;
import com.oleksandr.monolith.entity.enums.BOOKING_STATUS;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.repository.BookingRepository;
import com.oleksandr.monolith.repository.TicketRepository;
import com.oleksandr.monolith.repository.UserRepository;
import com.oleksandr.monolith.service.interfaces.BookingService;
import com.oleksandr.monolith.util.BookingMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, TicketRepository ticketRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        Booking booking = new Booking();

        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BOOKING_STATUS.CREATED);
        booking.setPaid(false);

        User user = userRepository.findById(userId).orElse(null);//todo exception
        booking.setUser(user);

        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);//todo exception
        booking.setTicket(ticket);

        Booking bookingToDto = bookingRepository.save(booking);
        return bookingMapper.mapToDto(bookingToDto);
    }

    @Override
    public List<BookingDTO> getBookingsByUser(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);//todo exception
        List<Booking> bookings = user.getBookings();
        return bookingMapper.mapEntityListToDtoList(bookings);
    }

    @Override
    public BookingDTO cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);//todo exception
        booking.setStatus(BOOKING_STATUS.CANCELLED);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDTO completeBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);//todo exception
        booking.setStatus(BOOKING_STATUS.PAID);
        return bookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public boolean isTicketAvailable(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null); //todo exception
        return ticket.getStatus() == TICKET_STATUS.AVAILABLE;
    }
}
