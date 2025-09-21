package com.oleksandr.monolith.Event.EntityRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByEventDateAfter(LocalDateTime date);
}
