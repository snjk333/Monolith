package com.oleksandr.monolith.Booking;

import com.oleksandr.monolith.Ticket.Ticket;
import com.oleksandr.monolith.User.User;
import com.oleksandr.monolith.Ticket.TICKET_STATUS;
import com.oleksandr.monolith.common.exceptions.*;
import com.oleksandr.monolith.User.UserService;
import com.oleksandr.monolith.Ticket.TicketService;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final TicketService ticketService;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              TicketService ticketService,
                              BookingMapper bookingMapper,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
    }

    /**
     * Creates a new booking for the given user and ticket.
     * Uses optimistic locking to prevent race conditions.
     */
    @Override
    @Transactional
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        log.info("Creating booking: userId={}, ticketId={}", userId, ticketId);

        User user = userService.getOrCreateUser(userId);
        Ticket ticket = ticketService.findEntityById(ticketId);

        if (ticket.getStatus() != TICKET_STATUS.AVAILABLE) {
            log.warn("Ticket {} is not available. Current status: {}", ticketId, ticket.getStatus());
            throw new TicketNotAvailableException("Ticket not available: " + ticketId);
        }

        bookingRepository.findByUserIdAndTicketId(user.getId(), ticket.getId())
                .ifPresent(b -> {
                    log.warn("Booking conflict detected: userId={}, ticketId={}", userId, ticketId);
                    throw new BookingConflictException("Booking already exists for ticket: " + ticket.getId());
                });

        try {
            // Reserve the ticket first
            ticket.setStatus(TICKET_STATUS.RESERVED);
            ticketService.save(ticket);

            // Create booking entity
            Booking booking = new Booking();
            booking.setCreatedAt(LocalDateTime.now());
            booking.setStatus(BOOKING_STATUS.CREATED);
            booking.setUser(user);
            booking.setTicket(ticket);

            Booking saved = bookingRepository.saveAndFlush(booking);
            BookingDTO dto = bookingMapper.mapToDto(saved);

            if (dto == null) {
                log.error("Booking saved but mapping failed: bookingId={}", booking.getId());
                throw new ResourceCorruptedException("Booking saved but could not map: " + booking.getId());
            }

            log.info("Booking successfully created: bookingId={}, userId={}, ticketId={}",
                    dto.getId(), dto.getUserId(), dto.getTicketId());
            return dto;

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            log.warn("Optimistic locking conflict while creating booking: userId={}, ticketId={}, message={}",
                    userId, ticketId, ole.getMessage());
            throw new ConcurrentUpdateException("Ticket was reserved by another user", ole);
        } catch (DataIntegrityViolationException dive) {
            log.error("Data integrity violation while creating booking: {}", dive.getMessage());
            throw new BookingConflictException("Booking already exists for ticket: " + ticketId, dive);
        }
    }

    /**
     * Retrieves all bookings for a given user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUser(UUID userId) {
        log.info("Fetching bookings for userId={}", userId);

        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        List<BookingDTO> dtos = bookingMapper.mapEntityListToDtoList(bookings);

        if (dtos.isEmpty() && !bookings.isEmpty()) {
            log.warn("Some bookings for userId={} failed to map to DTO", userId);
        }

        log.info("Found {} bookings for userId={}", dtos.size(), userId);
        return dtos;
    }

    /**
     * Cancels an existing booking.
     */
    @Override
    @Transactional
    public BookingDTO cancelBooking(UUID bookingId, UUID userId) {
        log.info("Cancelling booking: bookingId={}, userId={}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found: bookingId={}", bookingId);
                    return new ResourceNotFoundException("Booking not found: " + bookingId);
                });

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("Unauthorized booking cancellation attempt: bookingId={}, userId={}", bookingId, userId);
            throw new BookingAccessDeniedException("Cannot cancel another user's booking");
        }

        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.info("Booking {} is already cancelled", bookingId);
            return bookingMapper.mapToDto(booking);
        }

        try {
            Ticket ticket = ticketService.findEntityById(booking.getTicket().getId());
            booking.setStatus(BOOKING_STATUS.CANCELLED);
            ticket.setStatus(TICKET_STATUS.AVAILABLE);

            ticketService.save(ticket);
            Booking saved = bookingRepository.saveAndFlush(booking);

            BookingDTO dto = bookingMapper.mapToDto(saved);
            if (dto == null) {
                log.error("Booking {} cancelled but mapping failed", bookingId);
                throw new ResourceCorruptedException("Booking cancelled but could not map: " + bookingId);
            }

            log.info("Booking successfully cancelled: bookingId={}, userId={}, ticketId={}",
                    dto.getId(), dto.getUserId(), dto.getTicketId());
            return dto;

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            log.warn("Optimistic locking conflict while cancelling booking: bookingId={}, message={}",
                    bookingId, ole.getMessage());
            throw new ConcurrentUpdateException("Concurrent modification when cancelling booking " + bookingId, ole);
        }
    }

    /**
     * Marks a booking as paid (completed).
     */
    @Override
    @Transactional
    public BookingDTO completeBooking(UUID bookingId, UUID userId) {
        log.info("Completing booking: bookingId={}, userId={}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found: bookingId={}", bookingId);
                    return new ResourceNotFoundException("Booking not found: " + bookingId);
                });

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("Unauthorized booking completion attempt: bookingId={}, userId={}", bookingId, userId);
            throw new BookingAccessDeniedException("Cannot complete another user's booking");
        }

        if (booking.getStatus() == BOOKING_STATUS.PAID) {
            log.info("Booking {} already marked as PAID", bookingId);
            return bookingMapper.mapToDto(booking);
        }
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.warn("Attempted to complete a cancelled booking: bookingId={}", bookingId);
            throw new IllegalStateException("Cannot complete a cancelled booking: " + bookingId);
        }

        try {
            Ticket ticket = ticketService.findEntityById(booking.getTicket().getId());
            if (ticket.getStatus() == TICKET_STATUS.SOLD) {
                log.warn("Cannot complete booking {}: ticket {} is already SOLD", bookingId, ticket.getId());
                throw new TicketNotAvailableException("Ticket already sold: " + ticket.getId());
            }

            booking.setStatus(BOOKING_STATUS.PAID);
            ticket.setStatus(TICKET_STATUS.SOLD);

            ticketService.save(ticket);
            Booking saved = bookingRepository.saveAndFlush(booking);

            BookingDTO dto = bookingMapper.mapToDto(saved);
            if (dto == null) {
                log.error("Booking {} completed but mapping failed", bookingId);
                throw new ResourceCorruptedException("Booking completed but could not map: " + bookingId);
            }

            log.info("Booking successfully completed: bookingId={}, userId={}, ticketId={}",
                    dto.getId(), dto.getUserId(), dto.getTicketId());
            return dto;

        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            log.warn("Optimistic locking conflict while completing booking: bookingId={}, message={}",
                    bookingId, ole.getMessage());
            throw new ConcurrentUpdateException("Concurrent modification when completing booking " + bookingId, ole);
        } catch (DataIntegrityViolationException dive) {
            log.error("Data integrity violation while completing booking {}: {}", bookingId, dive.getMessage());
            throw new BookingConflictException("Conflict while completing booking " + bookingId, dive);
        }
    }

    /**
     * Checks if the ticket is available for booking.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isTicketAvailable(UUID ticketId) {
        Ticket ticket = ticketService.findEntityById(ticketId);
        boolean available = ticket.getStatus() == TICKET_STATUS.AVAILABLE;
        log.debug("Ticket {} availability: {}", ticketId, available);
        return available;
    }
}
