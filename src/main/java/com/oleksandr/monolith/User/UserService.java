package com.oleksandr.monolith.User;

import com.oleksandr.monolith.Booking.BookingDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // Получаем все бронирования пользователя
    List<BookingDTO> getUserBookings(UUID userId);

    // Создаём локальный профиль при первом обращении
    User getOrCreateUser(UUID userId); // дергаем AuthService, сохраняем локально
    UserDTO getUserDto(UUID userId);

    // Частичный апдейт (PATCH) — только локальные данные
    UserDTO updateUserInfo(UserDTO dto);
}
