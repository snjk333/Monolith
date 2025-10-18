package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.DTO.BookingCreateRequestDTO;
import com.oleksandr.monolith.Booking.DTO.BookingDetailsDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import com.oleksandr.monolith.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
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

    @GetMapping("/{id}")
    public BookingDetailsDTO getBookingDetails(@PathVariable UUID id)
    {
        return bookingCoordinator.getBookingDetails(id);
    }

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

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> initiatePayment(
            @PathVariable("id") UUID bookingId,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = jwtUtil.extractUserId(token);

        String customerIp = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(customerIp) || "127.0.0.1".equals(customerIp)) {
            customerIp = "192.168.0.1";
        }
        log.info("Initiating payment for booking {} from IP: {}", bookingId, customerIp);

        String redirectUrl = bookingCoordinator.initiatePayment(bookingId, userId, customerIp);

        return ResponseEntity.ok(redirectUrl);
    }
}
