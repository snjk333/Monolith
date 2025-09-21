package com.oleksandr.monolith.User.Service;

import com.oleksandr.monolith.User.EntityRepo.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {

    @Transactional
    User getOrCreateUser(UUID userId);
}
