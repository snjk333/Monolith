package com.oleksandr.monolith.repository;

import com.oleksandr.monolith.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
