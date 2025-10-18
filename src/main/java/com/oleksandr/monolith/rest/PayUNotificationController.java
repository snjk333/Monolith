package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.Service.BookingService;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import com.oleksandr.monolith.integration.payU.dto.PayUNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payu")
public class PayUNotificationController {

    private final BookingCoordinator bookingCoordinator;

    public PayUNotificationController(BookingCoordinator bookingCoordinator) {
        this.bookingCoordinator = bookingCoordinator;
    }

    @PostMapping("/notifications")
    public ResponseEntity<Void> handlePayUNotification(
            @RequestBody PayUNotificationDTO notification) {
        log.info("======================================================");
        log.info("           RECEIVED NOTIFICATION FROM PAYU!           ");
        log.info("======================================================");
        
        try {
            log.info("🔍 Notification object: {}", notification);
            log.info("🔍 Notification is null? {}", notification == null);
            if (notification != null) {
                log.info("🔍 Order is null? {}", notification.getOrder() == null);
                log.info("🔍 Order object: {}", notification.getOrder());
            }

            if (notification == null || notification.getOrder() == null) {
                log.error("❌ Notification does not contain order information!");
                log.error("❌ notification == null: {}", notification == null);
                if (notification != null) {
                    log.error("❌ notification.getOrder() == null: {}", notification.getOrder() == null);
                }
                return ResponseEntity.badRequest().build();
            }

            PayUNotificationDTO.Order order = notification.getOrder();
            String extOrderId = order.getExtOrderId();
            String status = order.getStatus();
            String payuOrderId = order.getOrderId();

            log.info("📦 Order ID (PayU): {}", payuOrderId);
            log.info("📦 External Order ID (Booking): {}", extOrderId);
            log.info("📊 Payment Status: {}", status);
            log.info("💰 Total Amount: {} {}", order.getTotalAmount(), order.getCurrencyCode());

            if (extOrderId == null || extOrderId.isEmpty()) {
                log.error("❌ extOrderId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            UUID bookingId;
            try {
                bookingId = UUID.fromString(extOrderId);
            } catch (IllegalArgumentException e) {
                log.error("❌ Invalid UUID format for extOrderId: {}", extOrderId);
                return ResponseEntity.badRequest().build();
            }

            switch (status) {
                case "COMPLETED":
                    log.info("✅ Payment COMPLETED for booking {}", bookingId);
                    handleCompletedPayment(bookingId, order);
                    break;

                case "PENDING":
                    log.info("⏳ Payment PENDING for booking {}", bookingId);
                    break;

                case "WAITING_FOR_CONFIRMATION":
                    log.info("⏳ Payment WAITING_FOR_CONFIRMATION for booking {}", bookingId);
                    break;

                case "CANCELED":
                    log.warn("❌ Payment CANCELED for booking {}", bookingId);
                    handleCanceledPayment(bookingId, order);
                    break;

                default:
                    log.warn("⚠️ Unknown payment status: {} for booking {}", status, bookingId);
            }

            log.info("======================================================");

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("❌ Error processing PayU notification", e);
            return ResponseEntity.ok().build();
        }
    }

    private void handleCompletedPayment(UUID bookingId, PayUNotificationDTO.Order order) {
        try {
            log.info("💳 Processing completed payment for booking: {}", bookingId);

            var booking = bookingCoordinator.getBookingDetails(bookingId);

            UUID userId = booking.getUser().getId();
            
            log.info("👤 User ID from booking: {}", userId);
            log.info("🎫 Completing booking for user {} and booking {}", userId, bookingId);

            var completedBooking = bookingCoordinator.completeBooking(bookingId, userId);
            
            log.info("✅ Booking {} successfully completed!", bookingId);
            log.info("📊 New booking status: {}", completedBooking.getStatus());
            
        } catch (Exception e) {
            log.error("❌ Failed to complete booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to complete booking after payment", e);
        }
    }

    private void handleCanceledPayment(UUID bookingId, PayUNotificationDTO.Order order) {
        try {
            log.info("🚫 Processing canceled payment for booking: {}", bookingId);

            var booking = bookingCoordinator.getBookingDetails(bookingId);

            UUID userId = booking.getUser().getId();
            
            log.info("👤 User ID from booking: {}", userId);
            log.info("❌ Canceling booking for user {} and booking {}", userId, bookingId);

            var canceledBooking = bookingCoordinator.cancelBooking(bookingId, userId);
            
            log.info("✅ Booking {} successfully canceled", bookingId);
            log.info("📊 New booking status: {}", canceledBooking.getStatus());
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel booking {}: {}", bookingId, e.getMessage(), e);
        }
    }


}