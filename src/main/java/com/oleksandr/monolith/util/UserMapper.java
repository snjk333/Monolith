package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    private final BookingMapper bookingMapper;

    public UserMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    // Entity → DTO
    public UserDTO mapToDto(User user) {
        if (user == null) return null;

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
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setBookings(new ArrayList<>()); // пустой список, позже маппится отдельно
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
        if (users == null || users.isEmpty()) return List.of();
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }
}
