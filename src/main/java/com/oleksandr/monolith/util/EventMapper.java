package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    // DTO → Entity
    public Event mapToEntity(EventDTO dto) {
        if (dto == null) return null;

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setImageURL(dto.getImageURL());
        event.setEventDate(dto.getEventDate()); // LocalDateTime теперь напрямую

        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(event));
            event.setTickets(tickets);
        } else {
            event.setTickets(List.of());
        }

        return event;
    }

    // Обновление существующей сущности
    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto == null) return eventToChange;

        if (dto.getName() != null) eventToChange.setName(dto.getName());
        if (dto.getDescription() != null) eventToChange.setDescription(dto.getDescription());
        if (dto.getLocation() != null) eventToChange.setLocation(dto.getLocation());
        if (dto.getImageURL() != null) eventToChange.setImageURL(dto.getImageURL());
        if (dto.getEventDate() != null) eventToChange.setEventDate(dto.getEventDate());

        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(eventToChange));
            eventToChange.setTickets(tickets);
        }

        return eventToChange;
    }

    // Entity → DTO
    public EventDTO mapToDto(Event event) {
        if (event == null) return null;

        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate()) // LocalDateTime напрямую
                .tickets(event.getTickets() != null
                        ? ticketMapper.mapEntityListToDtoList(event.getTickets())
                        : List.of())
                .build();
    }

    public List<EventDTO> mapListToDtoList(List<Event> events) {
        if (events == null || events.isEmpty()) return List.of();
        return events.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
