package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.DTO.BookingCreateRequestDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDetailsDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingsController {

    private final BookingCoordinator bookingCoordinator;

    public BookingsController(BookingCoordinator bookingCoordinator) {
        this.bookingCoordinator = bookingCoordinator;
    }

    //POST /bookings → забронировать билет
    @PostMapping
    public BookingSummaryDTO createBooking(@RequestBody BookingCreateRequestDTO bookingDTO)
    {
        return bookingCoordinator.createBooking(bookingDTO.getUserId(), bookingDTO.getTicketId());
    }

    //GET /bookings/{id} → детали бронирования
    @GetMapping("/{id}")
    public BookingDetailsDTO getBookingDetails(@PathVariable UUID id)
    {
        return bookingCoordinator.getBookingDetails(id);
    }

    //PUT /bookings/{id}/cancel → отменить бронирование
    @PutMapping("/{id}/cancel")
    public BookingSummaryDTO cancelBooking(@PathVariable UUID id, @RequestParam UUID userId){
        return bookingCoordinator.cancelBooking(id, userId);
    }

    //PUT /bookings/{id}/confirm → подтвердить оплату (например, после транзакции)
    @PutMapping("/{id}/confirm")
    public BookingSummaryDTO completeBooking(@PathVariable UUID id, @RequestParam UUID userId){
        return bookingCoordinator.completeBooking(id, userId);
    }
}
