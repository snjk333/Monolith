package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class EventMapper {

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    public Event mapToEntity(EventDTO dto) {
        if (dto == null) return null;

        LocalDateTime eventDate = null;
        if (dto.getEventDate() != null) {
            eventDate = LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(eventDate);
        event.setImageURL(dto.getImageURL());

        List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
        tickets.forEach(t -> t.setEvent(event)); // Важно: привязка tickets к event
        event.setTickets(tickets);

        return event;
    }

    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto.getName() != null) eventToChange.setName(dto.getName());
        if (dto.getDescription() != null) eventToChange.setDescription(dto.getDescription());
        if (dto.getLocation() != null) eventToChange.setLocation(dto.getLocation());
        if (dto.getImageURL() != null) eventToChange.setImageURL(dto.getImageURL());
        if (dto.getEventDate() != null) {
            eventToChange.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(eventToChange));
            eventToChange.setTickets(tickets);
        }
        return eventToChange;
    }

    public EventDTO mapToDto(Event event) {
        if (event == null) return null;

        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate() != null
                        ? event.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .tickets(event.getTickets() != null
                        ? ticketMapper.mapEntityListToDtoList(event.getTickets())
                        : List.of())
                .build();
    }

}

