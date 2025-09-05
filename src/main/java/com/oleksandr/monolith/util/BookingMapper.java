package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {
    public BookingDTO mapToDto(Booking booking) {
        //todo
        throw new RuntimeException("Not implemented yet");
    }

    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        //todo
        throw new RuntimeException("Not implemented yet");
    }
}
