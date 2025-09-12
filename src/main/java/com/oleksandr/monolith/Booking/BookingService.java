package com.oleksandr.monolith.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    // Основное
    BookingDTO createBooking(UUID userId, UUID ticketId);
    List<BookingDTO> getBookingsByUser(UUID userId);

    // Второстепенные
    BookingDTO cancelBooking(UUID bookingId, UUID userId);
    BookingDTO completeBooking(UUID bookingId, UUID userId);
    boolean isTicketAvailable(UUID ticketId);
}
