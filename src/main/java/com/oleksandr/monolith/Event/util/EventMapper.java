package com.oleksandr.monolith.Event.util;

import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import com.oleksandr.monolith.Ticket.util.TicketMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EventMapper {

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    // DTO → Entity
    public Event mapToEntity(EventDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EventDTO cannot be null");

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setImageURL(dto.getImageURL());
        event.setEventDate(dto.getEventDate());

        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(event));
            event.setTickets(tickets);
        } else {
            event.setTickets(List.of());
        }

        return event;
    }

    // PATCH-подход: частичное обновление
    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto == null) return eventToChange;

        if (dto.getName() != null) eventToChange.setName(dto.getName());
        if (dto.getDescription() != null) eventToChange.setDescription(dto.getDescription());
        if (dto.getLocation() != null) eventToChange.setLocation(dto.getLocation());
        if (dto.getImageURL() != null) eventToChange.setImageURL(dto.getImageURL());
        if (dto.getEventDate() != null) eventToChange.setEventDate(dto.getEventDate());

        if (dto.getTickets() != null) {
            // PATCH: обновляем существующие или добавляем новые тикеты
            List<Ticket> updatedTickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            for (Ticket updated : updatedTickets) {
                if (updated.getId() == null) {
                    updated.setEvent(eventToChange);
                    eventToChange.getTickets().add(updated);
                } else {
                    boolean found = false;
                    for (int i = 0; i < eventToChange.getTickets().size(); i++) {
                        Ticket current = eventToChange.getTickets().get(i);
                        if (current.getId().equals(updated.getId())) {
                            updated.setEvent(eventToChange);
                            eventToChange.getTickets().set(i, updated);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        updated.setEvent(eventToChange);
                        eventToChange.getTickets().add(updated);
                    }
                }
            }
        }

        return eventToChange;
    }

    // Entity → DTO
    public EventDTO mapToDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .tickets(event.getTickets() != null
                        ? ticketMapper.mapEntityListToDtoList(event.getTickets())
                        : List.of())
                .build();
    }

    public List<EventDTO> mapListToDtoList(List<Event> events) {
        return events == null ? List.of() :
                events.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<EventSummaryDTO> mapListToSummaryList(List<Event> allEvents) {
        return allEvents == null ? List.of() :
                allEvents.stream()
                        .map(this::mapToSummaryDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    // Entity → DTO
    public EventSummaryDTO mapToSummaryDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventSummaryDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .build();
    }

    public EventDetailsDTO mapEventToDetailsDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventDetailsDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .build();
    }
}
