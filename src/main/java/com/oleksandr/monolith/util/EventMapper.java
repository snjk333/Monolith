package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
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


    public EventDTO mapToDto(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .imageURL(event.getImageURL())
                .tickets(ticketMapper.mapEntityListToDtoList(event.getTickets()))
                .eventDate(String.valueOf(event.getEventDate()))
                .location(event.getLocation())
                .build();
    }

    public Event mapToEntity(EventDTO dto) {

        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate());
        List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());

        return new Event(dto.getId(), dto.getName(), dto.getDescription(),
                dto.getLocation(), eventDate, dto.getImageURL(),
                tickets);

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
