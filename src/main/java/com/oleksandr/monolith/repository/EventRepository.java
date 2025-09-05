package com.oleksandr.monolith.repository;

import com.oleksandr.monolith.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByEventDateAfter(LocalDateTime date);
}
