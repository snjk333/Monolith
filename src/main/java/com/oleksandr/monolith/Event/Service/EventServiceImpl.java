package com.oleksandr.monolith.Event.Service;

import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.monolith.Event.util.EventMapper;
import com.oleksandr.monolith.Event.EntityRepo.EventRepository;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
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
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventSummaryDTO> getAllEventsSummary() {
        return eventMapper.mapListToSummaryList(this.getAllEvents());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailsDTO getEventDetails(UUID id) {
        Event event = findById(id);
        return eventMapper.mapEventToDetailsDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return events;
    }

    @Transactional
    @Override
    public Event saveEventEntity(Event event) {
        return eventRepository.saveAndFlush(event);
    }


}
