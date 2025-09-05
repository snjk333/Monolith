package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.repository.EventRepository;
import com.oleksandr.monolith.service.interfaces.tmp_withRealization.EventService;
import com.oleksandr.monolith.util.EventMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.mapListToDtoList(events);//todo
    }

    @Override
    public EventDTO getEventById(UUID eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
        return eventMapper.mapToDto(event); //todo
    }

    @Override
    public List<EventDTO> getUpcomingEvents(){
        List<Event> events =
                new ArrayList<>(
                        eventRepository.findAll().stream().filter(
                                event -> event.getEventDate()
                                        .isAfter(LocalDateTime.now())).toList());
        return eventMapper.mapListToDtoList(events);//todo

    }

    @Override
    public EventDTO createEvent(EventDTO dto) {
        Event eventFromDTO = eventMapper.mapToEntity(dto);//todo
        Event savedEvent = eventRepository.save(eventFromDTO);
        return eventMapper.mapToDto(savedEvent);
    }

    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO dto) {
        Event eventToChange = eventRepository.findById(eventId).orElse(null);//todo exeption
        Event UpdatedEvent = eventMapper.updateEventInformation(eventToChange, dto);//todo exeption
        eventRepository.save(UpdatedEvent);//todo exeption
        return dto;
    }


    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }
}
