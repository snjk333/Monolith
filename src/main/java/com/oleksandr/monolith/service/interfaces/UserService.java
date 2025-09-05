package com.oleksandr.monolith.service.interfaces;

import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.dto.BookingDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // Получаем все бронирования пользователя
    List<BookingDTO> getUserBookings(UUID userId);

    // Создаём локальный профиль при первом обращении
    UserDTO getOrCreateUser(UUID userId); // дергаем AuthService, сохраняем локально

    // Частичный апдейт (PATCH) — только локальные данные
    UserDTO updateUser(UserDTO dto);
}
