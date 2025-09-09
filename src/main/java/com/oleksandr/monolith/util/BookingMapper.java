package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.enums.BOOKING_STATUS;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingDTO mapToDto(Booking booking) {
        if (booking == null) return null;

        return BookingDTO.builder()
                .id(booking.getId())
                .ticketId(booking.getTicket() != null ? booking.getTicket().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public Booking mapToEntity(BookingDTO dto) {
        if (dto == null) return null;

        Booking booking = new Booking();
        booking.setId(dto.getId());
        // ticket и user надо будет устанавливать отдельно в сервисе через репозитории
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return List.of();
        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<Booking> mapDtoListToEntityList(List<BookingDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
