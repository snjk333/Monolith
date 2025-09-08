package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.User;
import com.oleksandr.monolith.entity.enums.BOOKING_STATUS;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.exceptions.*;
import com.oleksandr.monolith.repository.BookingRepository;
import com.oleksandr.monolith.service.interfaces.UserService;
import com.oleksandr.monolith.service.interfaces.BookingService;
import com.oleksandr.monolith.service.interfaces.TicketService;
import com.oleksandr.monolith.util.BookingMapper;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.management.RuntimeMBeanException;
import java.nio.file.AccessDeniedException;
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

    @Override
    @Transactional
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        User user = userService.getOrCreateUser(userId);
        Ticket ticket = ticketService.findEntityById(ticketId);

        if (ticket.getStatus() != TICKET_STATUS.AVAILABLE) {
            throw new TicketNotAvailableException("Ticket not available: " + ticketId);
        }

        bookingRepository.findByUserIdAndTicketId(user.getId(), ticket.getId())
                .ifPresent(b -> {
                    throw new BookingConflictException("Booking already exists for ticket: " + ticket.getId());
                });

        try {
            // резервируем билет
            ticket.setStatus(TICKET_STATUS.RESERVED);
            ticketService.save(ticket); // saveAndFlush с @Version

            // создаём бронирование

            Booking booking = new Booking();
            booking.setCreatedAt(LocalDateTime.now());
            booking.setStatus(BOOKING_STATUS.CREATED);
            booking.setUser(user);
            booking.setTicket(ticket);

            Booking saved = bookingRepository.saveAndFlush(booking);
            return bookingMapper.mapToDto(saved);

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            throw new ConcurrentUpdateException("Ticket was reserved by another user", ole);
        } catch (DataIntegrityViolationException dive) {
            throw new BookingConflictException("Booking already exists for ticket: " + ticketId, dive);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUser(UUID userId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        return bookingMapper.mapEntityListToDtoList(bookings);
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingAccessDeniedException("Cannot cancel another user's booking");
        }

        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            return bookingMapper.mapToDto(booking);
        }

        try {
            Ticket ticket = ticketService.findEntityById(booking.getTicket().getId());
            booking.setStatus(BOOKING_STATUS.CANCELLED);
            ticket.setStatus(TICKET_STATUS.AVAILABLE);

            ticketService.save(ticket);
            Booking saved = bookingRepository.saveAndFlush(booking);
            return bookingMapper.mapToDto(saved);

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            throw new ConcurrentUpdateException("Concurrent modification when cancelling booking " + bookingId, ole);
        }
    }

    @Override
    @Transactional
    public BookingDTO completeBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingAccessDeniedException("Cannot complete another user's booking");
        }

        if (booking.getStatus() == BOOKING_STATUS.PAID) {
            return bookingMapper.mapToDto(booking);
        }
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled booking: " + bookingId);
        }

        try {
            Ticket ticket = ticketService.findEntityById(booking.getTicket().getId());
            if (ticket.getStatus() == TICKET_STATUS.SOLD) {
                throw new TicketNotAvailableException("Ticket already sold: " + ticket.getId());
            }

            booking.setStatus(BOOKING_STATUS.PAID);
            ticket.setStatus(TICKET_STATUS.SOLD);

            ticketService.save(ticket);
            Booking saved = bookingRepository.saveAndFlush(booking);
            return bookingMapper.mapToDto(saved);

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            throw new ConcurrentUpdateException("Concurrent modification when completing booking " + bookingId, ole);
        } catch (DataIntegrityViolationException dive) {
            throw new BookingConflictException("Conflict while completing booking " + bookingId, dive);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTicketAvailable(UUID ticketId) {
        Ticket ticket = ticketService.findEntityById(ticketId);
        return ticket.getStatus() == TICKET_STATUS.AVAILABLE;
    }

}
