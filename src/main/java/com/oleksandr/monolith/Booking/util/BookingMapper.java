package com.oleksandr.monolith.Booking.util;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class BookingMapper {

    public BookingDTO mapToDto(Booking booking) {
        if (booking == null) throw new IllegalArgumentException("Booking entity cannot be null");

        return BookingDTO.builder()
                .id(booking.getId())
                .ticketId(booking.getTicket() != null ? booking.getTicket().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public Booking mapToEntity(BookingDTO dto) {
        if (dto == null) throw new IllegalArgumentException("BookingDTO cannot be null");

        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStatus(dto.getStatus());
        // ticket и user подгружаем через сервис
        return booking;
    }

    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        return bookings == null ? List.of() :
                bookings.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<Booking> mapDtoListToEntityList(List<BookingDTO> dtos) {
        return dtos == null ? List.of() :
                dtos.stream()
                        .map(this::mapToEntity)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public BookingSummaryDTO mapToSummaryDto(Booking booking) {
        if (booking == null) throw new IllegalArgumentException("Booking entity cannot be null");

        return BookingSummaryDTO.builder()
                .id(booking.getId())
                .ticketId(booking.getTicket() != null ? booking.getTicket().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}