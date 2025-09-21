package com.oleksandr.monolith.Event.Service;

import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.monolith.Event.DTO.EventDTO;
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
