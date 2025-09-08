package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.repository.EventRepository;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.util.EventMapper;
import com.oleksandr.monolith.util.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

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
        List<Event> events = eventRepository.findAll();
        return eventMapper.mapListToDtoList(events);//todo
    }

    @Transactional(readOnly = true)
    @Override
    public EventDTO getEventById(UUID eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));;
        return eventMapper.mapToDto(event); //todo
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getUpcomingEvents(){
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return eventMapper.mapListToDtoList(events);//todo

    }

    @Transactional
    @Override
    public EventDTO createEvent(EventDTO dto) {
        if (dto.getId() != null && eventRepository.existsById(dto.getId())) {
            throw new ResourceAlreadyExistsException("Event with id " + dto.getId() + " already exists");
        }

        Event event = eventMapper.mapToEntity(dto);
        Event savedEvent = eventRepository.saveAndFlush(event);
        return eventMapper.mapToDto(savedEvent);
    }




    @Transactional
    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO dto) {
        Event eventToChange = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        Event updatedEvent = eventMapper.updateEventInformation(eventToChange, dto);
        Event savedEvent = eventRepository.saveAndFlush(updatedEvent);
        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public Event findById(UUID eventID) {
        return eventRepository.findById(eventID)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found")); //todo exception
    }
}
