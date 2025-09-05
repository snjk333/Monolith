package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {
    public BookingDTO mapToDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .status(booking.getStatus().toString())
                .ticketId(booking.getTicket().getId())
                .userId(booking.getUser().getId())
                .build();
    }

    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }
}
