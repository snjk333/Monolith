package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private static final Logger log = LoggerFactory.getLogger(UserMapper.class);

    private final BookingMapper bookingMapper;

    public UserMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    public UserDTO mapToDto(User user) {
        if (user == null) {
            log.warn("Received null User entity, returning null UserDTO");
            return null;
        }
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bookings(bookingMapper.mapEntityListToDtoList(user.getBookings()))
                .build();
        log.info("Mapped User entity (id={}) to UserDTO successfully with {} bookings",
                user.getId(), user.getBookings() != null ? user.getBookings().size() : 0);
        return dto;
    }

    public User mapToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            log.warn("Received null UserDTO, returning null User entity");
            return null;
        }
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (user.getBookings() == null) {
            user.setBookings(new ArrayList<>());
        }
        log.info("Mapped UserDTO (id={}) to User entity successfully", userDTO.getId());
        return user;
    }

    public User updateUserInformation(User userToChange, UserDTO dto) {
        if (dto.getUsername() != null) {
            userToChange.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            userToChange.setEmail(dto.getEmail());
        }
        log.info("Updated User entity (id={}) with new information", userToChange.getId());
        return userToChange;
    }
}
