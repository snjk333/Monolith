package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.DTO.BookingCreateRequestDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingsController {

    //POST /bookings → забронировать билет
    @PostMapping
    public BookingDTO createBooking(@RequestBody BookingCreateRequestDTO bookingDTO)
    {
        //todo
        return null;
    }

    //GET /bookings/{id} → детали бронирования
    @GetMapping("/{id}")
    public BookingDTO getBookingDetails(@PathVariable Long id)
    {
        //todo
        return null;
    }

    //PUT /bookings/{id}/cancel → отменить бронирование
    @PutMapping("/{id}/cancel")
    public BookingDTO cancelBooking(@PathVariable Long id){
        //todo
        return null;
    }

    //PUT /bookings/{id}/confirm → подтвердить оплату (например, после транзакции)
    @PutMapping("/{id}/confirm")
    public BookingDTO confirmBooking(@PathVariable Long id){
        //todo
        return null;
    }
}
