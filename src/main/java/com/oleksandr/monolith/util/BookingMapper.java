package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class BookingMapper {

    private static final Logger log = LoggerFactory.getLogger(BookingMapper.class);

    public BookingDTO mapToDto(Booking b) {
        if (b == null) {
            log.warn("BookingMapper.mapToDto called with null Booking -> returning null");
            return null;
        }

        // Defensive mapping: lazy fields (ticket/user) may be uninitialized if entity is detached.
        // We access them carefully and catch unexpected runtime errors per-field.
        Object ticketId = null;
        Object userId = null;

        try {
            if (b.getTicket() != null) {
                ticketId = b.getTicket().getId();
            } else {
                log.debug("Booking {} has null ticket reference", b.getId());
            }
        } catch (RuntimeException ex) {
            // Could be LazyInitializationException or other runtime issue when accessing proxy
            log.warn("Failed to read ticket from Booking {} — likely detached or lazy init issue: {}",
                    b.getId(), ex.toString());
            ticketId = null;
        }

        try {
            if (b.getUser() != null) {
                userId = b.getUser().getId();
            } else {
                log.debug("Booking {} has null user reference", b.getId());
            }
        } catch (RuntimeException ex) {
            log.warn("Failed to read user from Booking {} — likely detached or lazy init issue: {}",
                    b.getId(), ex.toString());
            userId = null;
        }

        String status = null;
        try {
            status = b.getStatus() != null ? b.getStatus().name() : null;
            if (status == null) {
                log.debug("Booking {} has null status", b.getId());
            }
        } catch (RuntimeException ex) {
            log.warn("Failed to read status from Booking {}: {}", b.getId(), ex.toString());
            status = null;
        }

        // Build DTO
        try {
            return BookingDTO.builder()
                    .id(b.getId())
                    .status(status)
                    .ticketId(ticketId == null ? null : (java.util.UUID) ticketId)
                    .userId(userId == null ? null : (java.util.UUID) userId)
                    .build();
        } catch (ClassCastException cce) {
            log.error("Type mismatch while mapping Booking {} → BookingDTO: {}", b.getId(), cce.toString());
            return null;
        } catch (RuntimeException ex) {
            log.error("Unexpected error while mapping Booking {} → BookingDTO: {}", b.getId(), ex.toString());
            return null;
        }
    }


    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        if (bookings == null) {
            log.warn("BookingMapper.mapEntityListToDtoList called with null list -> returning empty list");
            return List.of();
        }

        // Map and filter out any nulls produced by mapToDto (mapper returns null on error)
        List<BookingDTO> dtos = bookings.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .toList();

        if (dtos.size() < bookings.size()) {
            log.info("Some bookings were skipped during mapping because mapToDto returned null (possible lazy init or errors). " +
                    "Input size: {}, Output size: {}", bookings.size(), dtos.size());
        }

        return dtos;
    }
}
