package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Booking.DTO.BookingSummaryDTO;
import com.oleksandr.monolith.User.DTO.UserDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.DTO.UserUpdateRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    // GET /users/{id} → получить профиль пользователя (детально)
    @GetMapping("/{id}")
    public UserDTO getUserProfile(@PathVariable UUID id) {
        // TODO: сервис вытаскивает юзера и маппит в UserDTO
        return null;
    }

    // PATCH /users/{id} → обновить профиль
    @PatchMapping("/{id}")
    public UserSummaryDTO updateUserProfile(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequestDTO request
    ) {
        // TODO: сервис апдейтит username/email и возвращает summary
        return null;
    }

    // GET /users/{id}/bookings → список бронирований пользователя
    @GetMapping("/{id}/bookings")
    public List<BookingSummaryDTO> getUserBookings(@PathVariable UUID id) {
        // TODO: сервис отдаёт список броней этого юзера
        return null;
    }




}


/*
GET /users/{id} → получить профиль пользователя
PATCH /users/{id} → обновить профиль
GET /users/{id}/bookings → список бронирований пользователя
 */