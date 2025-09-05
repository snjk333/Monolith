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

    TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    public List<EventDTO> mapListToDtoList(List<Event> events) {
        return events.stream()
                .map(this::mapToDto)
                .toList();
    }


    public EventDTO mapToDto(Event e) {
        if (e == null) return null;
        return EventDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .imageURL(e.getImageURL())
                .tickets(e.getTickets() != null ? ticketMapper.mapEntityListToDtoList(e.getTickets()) : List.of())
                .eventDate(e.getEventDate() != null ? e.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .location(e.getLocation())
                .build();
    }

    public Event mapToEntity(EventDTO dto) {
        if (dto == null) return null;
        LocalDateTime eventDate = null;
        if (dto.getEventDate() != null) {
            eventDate = LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets()); // returns empty list if null
        return new Event(dto.getId(), dto.getName(), dto.getDescription(),
                dto.getLocation(), eventDate, dto.getImageURL(), tickets);
    }

    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto.getName() != null) {
            eventToChange.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            eventToChange.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            eventToChange.setLocation(dto.getLocation());
        }
        if (dto.getImageURL() != null) {
            eventToChange.setImageURL(dto.getImageURL());
        }
        if (dto.getEventDate() != null) {
            eventToChange.setEventDate(LocalDateTime.parse(dto.getEventDate()));
        }
        if (dto.getTickets() != null) {
            eventToChange.setTickets(ticketMapper.mapTicketsListFromDto(dto.getTickets()));
        }
        return eventToChange;
    }


}
