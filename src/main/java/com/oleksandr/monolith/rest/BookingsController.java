package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.DTO.BookingCreateRequestDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDetailsDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import com.oleksandr.monolith.common.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingsController {

    private final BookingCoordinator bookingCoordinator;
    private final JwtUtil jwtUtil;

    public BookingsController(BookingCoordinator bookingCoordinator, JwtUtil jwtUtil) {
        this.bookingCoordinator = bookingCoordinator;
        this.jwtUtil = jwtUtil;
    }

    //POST /bookings → забронировать билет
    @PostMapping
    public ResponseEntity<BookingSummaryDTO> createBooking(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BookingCreateRequestDTO bookingDTO) {
        
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID userId = jwtUtil.extractUserId(token);
        BookingSummaryDTO booking = bookingCoordinator.createBooking(userId, bookingDTO.getTicketId());
        return ResponseEntity.ok(booking);
    }

    //GET /bookings/{id} → детали бронирования
    @GetMapping("/{id}")
    public BookingDetailsDTO getBookingDetails(@PathVariable UUID id)
    {
        return bookingCoordinator.getBookingDetails(id);
    }

    //PUT /bookings/{id}/cancel → отменить бронирование
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingSummaryDTO> cancelBooking(
            @PathVariable UUID id, 
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID userId = jwtUtil.extractUserId(token);
        BookingSummaryDTO booking = bookingCoordinator.cancelBooking(id, userId);
        return ResponseEntity.ok(booking);
    }

    //PUT /bookings/{id}/confirm → подтвердить оплату (например, после транзакции)
    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingSummaryDTO> completeBooking(
            @PathVariable UUID id, 
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID userId = jwtUtil.extractUserId(token);
        BookingSummaryDTO booking = bookingCoordinator.completeBooking(id, userId);
        return ResponseEntity.ok(booking);
    }

    //GET /bookings/my → получить бронирования текущего пользователя
    @GetMapping("/my")
    public ResponseEntity<List<BookingSummaryDTO>> getMyBookings(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID userId = jwtUtil.extractUserId(token);
        List<BookingSummaryDTO> bookings = bookingCoordinator.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
}
