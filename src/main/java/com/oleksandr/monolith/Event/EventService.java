package com.oleksandr.monolith.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {

    // Основное
    List<EventDTO> getAllEvents();
    EventDTO getEventDTOById(UUID eventId);
    List<EventDTO> getUpcomingEvents();

    // CRUD (админка, позже)
    EventDTO createEvent(EventDTO dto);
    EventDTO updateEvent(UUID eventId, EventDTO dto);
    void deleteEvent(UUID eventId);

    Event findById(UUID eventID);
}
