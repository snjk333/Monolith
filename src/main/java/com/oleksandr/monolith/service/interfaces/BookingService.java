package com.oleksandr.monolith.service.interfaces;

import com.oleksandr.monolith.dto.BookingDTO;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    // Основное
    BookingDTO createBooking(UUID userId, UUID ticketId);
    List<BookingDTO> getBookingsByUser(UUID userId);

    // Второстепенные
    BookingDTO cancelBooking(UUID bookingId);
    BookingDTO completeBooking(UUID bookingId);
    boolean isTicketAvailable(UUID ticketId);
}
