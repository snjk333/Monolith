package com.oleksandr.monolith.Event;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventDTO> getAllEvents();
    List<EventDTO> getUpcomingEvents();
    Event findById(UUID eventID);
    @Transactional
    Event saveEventEntity(Event event);
}
