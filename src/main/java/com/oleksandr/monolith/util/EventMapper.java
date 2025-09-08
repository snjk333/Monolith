package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private static final Logger log = LoggerFactory.getLogger(EventMapper.class);

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    public Event mapToEntity(EventDTO dto) {
        if (dto == null) {
            log.warn("Attempted to map null EventDTO to Event entity");
            return null;
        }

        LocalDateTime eventDate = null;
        if (dto.getEventDate() != null) {
            try {
                eventDate = LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                log.error("Failed to parse eventDate '{}' from EventDTO: {}", dto.getEventDate(), e.getMessage());
            }
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(eventDate);
        event.setImageURL(dto.getImageURL());

        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(event));
            event.setTickets(tickets);
            log.info("Mapped {} tickets to Event '{}'", tickets.size(), event.getName());
        } else {
            event.setTickets(List.of());
        }

        log.info("Mapped EventDTO '{}' to Event entity with ID {}", dto.getName(), dto.getId());
        return event;
    }

    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto == null) {
            log.warn("Attempted to update Event entity with null EventDTO");
            return eventToChange;
        }

        if (dto.getName() != null) eventToChange.setName(dto.getName());
        if (dto.getDescription() != null) eventToChange.setDescription(dto.getDescription());
        if (dto.getLocation() != null) eventToChange.setLocation(dto.getLocation());
        if (dto.getImageURL() != null) eventToChange.setImageURL(dto.getImageURL());

        if (dto.getEventDate() != null) {
            try {
                eventToChange.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (DateTimeParseException e) {
                log.error("Failed to parse eventDate '{}' while updating Event '{}': {}", dto.getEventDate(), eventToChange.getId(), e.getMessage());
            }
        }

        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(eventToChange));
            eventToChange.setTickets(tickets);
            log.info("Updated {} tickets for Event '{}'", tickets.size(), eventToChange.getId());
        }

        log.info("Updated Event entity '{}' from EventDTO", eventToChange.getId());
        return eventToChange;
    }

    public EventDTO mapToDto(Event event) {
        if (event == null) {
            log.warn("Attempted to map null Event entity to EventDTO");
            return null;
        }

        EventDTO dto = EventDTO.builder()
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

        log.info("Mapped Event entity '{}' to EventDTO '{}'", event.getId(), event.getName());
        return dto;
    }

    public List<EventDTO> mapListToDtoList(List<Event> events) {
        if (events == null) {
            log.warn("Attempted to map null Event list to EventDTO list");
            return List.of();
        }

        List<EventDTO> dtos = events.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        log.info("Mapped {} Event entities to EventDTO list", dtos.size());
        return dtos;
    }
}
