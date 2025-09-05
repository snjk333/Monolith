package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private final BookingMapper bookingMapper;

    public UserMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }


    public UserDTO mapToDto(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bookings(bookingMapper.mapEntityListToDtoList(user.getBookings()))
                .build();
    }

    public User mapToEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (user.getBookings() == null) user.setBookings(new ArrayList<>());
        return user;
    }
}
