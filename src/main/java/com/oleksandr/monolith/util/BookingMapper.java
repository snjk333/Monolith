package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingDTO mapToDto(Booking b) {
        if (b == null) return null;

        return BookingDTO.builder()
                .id(b.getId())
                .status(b.getStatus() != null ? b.getStatus().name() : null)
                .ticketId(b.getTicket() != null ? b.getTicket().getId() : null)
                .userId(b.getUser() != null ? b.getUser().getId() : null)
                .build();
    }


    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }
}
