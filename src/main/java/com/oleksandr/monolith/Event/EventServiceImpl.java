package com.oleksandr.monolith.Event;

import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.mapListToDtoList(events);
    }
    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return eventMapper.mapListToDtoList(events);
    }

    @Transactional
    @Override
    public Event saveEventEntity(Event event) {
        return eventRepository.saveAndFlush(event);
    }
}
