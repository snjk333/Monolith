package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.service.interfaces.UserService;

import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public List<BookingDTO> getUserBookings(UUID userId) {
        return List.of();
    }

    @Override
    public UserDTO getOrCreateUser(UUID userId) {
        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO dto) {
        return null;
    }
}
