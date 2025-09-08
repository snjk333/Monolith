package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.repository.EventRepository;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.util.EventMapper;
import com.oleksandr.monolith.util.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, TicketMapper ticketMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getAllEvents() {
        log.info("Fetching all events from repository");
        List<Event> events = eventRepository.findAll();
        log.info("Fetched {} events", events.size());
        return eventMapper.mapListToDtoList(events);
    }

    @Transactional(readOnly = true)
    @Override
    public EventDTO getEventDTOById(UUID eventId){
        log.info("Fetching event by ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event not found with ID: {}", eventId);
                    return new ResourceNotFoundException("Event not found with ID: " + eventId);
                });
        log.info("Event found: {}", event.getName());
        return eventMapper.mapToDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getUpcomingEvents(){
        log.info("Fetching upcoming events after {}", LocalDateTime.now());
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        log.info("Fetched {} upcoming events", events.size());
        return eventMapper.mapListToDtoList(events);
    }

    @Transactional
    @Override
    public EventDTO createEvent(EventDTO dto) {
        log.info("Creating new event with ID: {}", dto.getId());

        if (dto.getId() != null && eventRepository.existsById(dto.getId())) {
            log.warn("Event creation failed: Event with ID {} already exists", dto.getId());
            throw new ResourceAlreadyExistsException("Event with id " + dto.getId() + " already exists");
        }

        Event event = eventMapper.mapToEntity(dto);
        Event savedEvent = eventRepository.saveAndFlush(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional
    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO dto) {
        log.info("Updating event with ID: {}", eventId);

        Event eventToChange = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event update failed: Event not found with ID {}", eventId);
                    return new ResourceNotFoundException("Event not found: " + eventId);
                });

        Event updatedEvent = eventMapper.updateEventInformation(eventToChange, dto);
        Event savedEvent = eventRepository.saveAndFlush(updatedEvent);

        log.info("Event updated successfully with ID: {}", savedEvent.getId());
        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {
        log.info("Deleting event with ID: {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            log.warn("Event delete failed: Event not found with ID {}", eventId);
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }

        eventRepository.deleteById(eventId);
        log.info("Event deleted successfully with ID: {}", eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public Event findById(UUID eventId) {
        log.info("Finding event entity by ID: {}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event not found with ID: {}", eventId);
                    return new ResourceNotFoundException("Event not found with ID: " + eventId);
                });
    }
}
