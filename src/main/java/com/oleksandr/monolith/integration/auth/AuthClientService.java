package com.oleksandr.monolith.integration.auth;

import com.oleksandr.monolith.User.UserDTO;

import java.util.UUID;

public interface AuthClientService {
    UserDTO getUserById(UUID userId);

    UserDTO updateUser(UserDTO userDto);
}
