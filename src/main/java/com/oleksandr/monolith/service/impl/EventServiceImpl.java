package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.repository.EventRepository;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.util.EventMapper;
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


    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.mapListToDtoList(events);//todo
    }

    @Transactional(readOnly = true)
    @Override
    public EventDTO getEventById(UUID eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
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
        Event eventFromDTO = eventMapper.mapToEntity(dto);//todo
        Event savedEvent = eventRepository.save(eventFromDTO);
        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional
    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO dto) {
        Event eventToChange = eventRepository.findById(eventId).orElse(null);//todo exeption
        Event UpdatedEvent = eventMapper.updateEventInformation(eventToChange, dto);//todo exeption
        return eventMapper.mapToDto(eventRepository.save(UpdatedEvent));//todo exeption

    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public Event findById(UUID eventID) {
        return eventRepository.findById(eventID).orElse(null); //todo exception
    }
}
