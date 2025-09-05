package com.oleksandr.monolith.service.interfaces;

import com.oleksandr.monolith.dto.UserDTO;

import java.util.UUID;

public interface AuthClientService {
    UserDTO getUserById(UUID userId);
}
