package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserMapper {

    private final BookingMapper bookingMapper;

    public UserMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    // Entity → DTO
    public UserDTO mapToDto(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        List<BookingDTO> bookingsDto = user.getBookings() != null
                ? bookingMapper.mapEntityListToDtoList(user.getBookings())
                : List.of();

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .bookings(bookingsDto)
                .build();
    }

    // DTO → Entity
    public User mapToEntity(UserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("UserDTO cannot be null");

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setBookings(new ArrayList<>()); // bookings подгружаем отдельно через сервис
        return user;
    }

    // Обновление существующего пользователя
    public User updateUserInformation(User user, UserDTO dto) {
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        return user;
    }

    // Список сущностей → список DTO
    public List<UserDTO> mapListToDtoList(List<User> users) {
        return users == null ? List.of() :
                users.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }
}
