package com.oleksandr.monolith.service.interfaces.tmp_withRealization;

import com.oleksandr.monolith.dto.EventDTO;

import java.util.List;
import java.util.UUID;

public interface EventService {

    // Основное
    List<EventDTO> getAllEvents();
    EventDTO getEventById(UUID eventId);
    List<EventDTO> getUpcomingEvents();

    // CRUD (админка, позже)
    EventDTO createEvent(EventDTO dto);
    EventDTO updateEvent(UUID eventId, EventDTO dto);
    void deleteEvent(UUID eventId);
}
