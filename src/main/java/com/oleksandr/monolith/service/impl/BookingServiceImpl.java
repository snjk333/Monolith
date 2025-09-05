package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.repository.BookingRepository;
import com.oleksandr.monolith.service.interfaces.BookingService;
import com.oleksandr.monolith.util.BookingMapper;

import java.util.List;
import java.util.UUID;

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDTO createBooking(UUID userId, UUID ticketId) {
        return null;
    }

    @Override
    public List<BookingDTO> getBookingsByUser(UUID userId) {
        return List.of();
    }

    @Override
    public BookingDTO cancelBooking(UUID bookingId) {
        return null;
    }

    @Override
    public BookingDTO completeBooking(UUID bookingId) {
        return null;
    }

    @Override
    public boolean isTicketAvailable(UUID ticketId) {
        return false;
    }
}
