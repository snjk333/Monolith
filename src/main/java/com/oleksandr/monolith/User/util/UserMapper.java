package com.oleksandr.monolith.User.util;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.util.BookingMapper;
import com.oleksandr.monolith.User.DTO.AuthUserDTO;
import com.oleksandr.monolith.User.DTO.UserDTO;
import com.oleksandr.monolith.User.DTO.UserProfileResponseDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.EntityRepo.User;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;
import com.oleksandr.monolith.Wallet.util.WalletMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class UserMapper {

    private final BookingMapper bookingMapper;

    private final WalletMapper walletMapper;

    public UserMapper(BookingMapper bookingMapper, WalletMapper walletMapper) {
        this.bookingMapper = bookingMapper;
        this.walletMapper = walletMapper;
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
        user.setBookings(new ArrayList<>());
        user.setWallet(new Wallet());
        return user;
    }

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

    public UserSummaryDTO mapToSummaryDto(User user) {
        return UserSummaryDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User mapToEntityFromAuth(AuthUserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("AuthUserDTO cannot be null");

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setBookings(new ArrayList<>());
        return user;
    }

    public AuthUserDTO mapToAuthDto(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        return AuthUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public UserProfileResponseDTO mapProfileResponseDTO(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .walletDTO(walletMapper.mapToDto(user.getWallet()))
                .build();
    }
}