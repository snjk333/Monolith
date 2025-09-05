package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.service.interfaces.AuthClientService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthClientServiceImpl implements AuthClientService {
    @Override
    public UserDTO getUserById(UUID userId) {
        return null; //todo send HTTP request to Auth MS
    }
}
