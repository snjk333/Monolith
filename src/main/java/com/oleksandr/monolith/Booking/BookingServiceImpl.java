package com.oleksandr.monolith.Booking;

import com.oleksandr.monolith.common.exceptions.BookingConflictException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.User.User;
import com.oleksandr.monolith.Ticket.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Создает сущность бронирования. Содержит проверку на дубликаты.
     */
    @Transactional
    @Override
    public Booking createBooking(User user, Ticket ticket) {
        log.info("Creating booking entity for userId={} and ticketId={}", user.getId(), ticket.getId());
        // Проверка на существующее бронирование
        bookingRepository.findByUserIdAndTicketId(user.getId(), ticket.getId())
                .ifPresent(b -> {
                    log.warn("Booking conflict detected: userId={}, ticketId={}", user.getId(), ticket.getId());
                    throw new BookingConflictException("Booking already exists for ticket: " + ticket.getId());
                });

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTicket(ticket);
        booking.setStatus(BOOKING_STATUS.CREATED);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.saveAndFlush(booking);
    }

    /**
     * Отменяет бронирование.
     */
    @Transactional
    @Override
    public Booking cancelBooking(Booking booking) {
        log.info("Cancelling booking entity with id={}", booking.getId());
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.info("Booking {} is already cancelled", booking.getId());
            return booking;
        }
        booking.setStatus(BOOKING_STATUS.CANCELLED);
        return bookingRepository.saveAndFlush(booking);
    }

    /**
     * Завершает бронирование (оплачено).
     */
    @Transactional
    @Override
    public Booking completeBooking(Booking booking) {
        log.info("Completing booking entity with id={}", booking.getId());
        if (booking.getStatus() == BOOKING_STATUS.PAID) {
            log.info("Booking {} already marked as PAID", booking.getId());
            return booking;
        }
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.warn("Attempted to complete a cancelled booking: bookingId={}", booking.getId());
            throw new IllegalStateException("Cannot complete a cancelled booking: " + booking.getId());
        }
        booking.setStatus(BOOKING_STATUS.PAID);
        return bookingRepository.saveAndFlush(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking findById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getBookingsByUser(UUID userId) {
        return bookingRepository.findAllByUserId(userId);
    }
}