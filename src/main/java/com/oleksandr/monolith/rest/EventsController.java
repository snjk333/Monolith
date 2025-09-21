package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventsController {

    @GetMapping
    public List<EventDTO> getAllEvents()
    {
        return null;
        //todo
    }

    @GetMapping("/{id}")
    public EventDTO getEventDetails( @PathVariable("id") UUID id) {
        return null;
        //todo
    }

    @GetMapping("/{id}/tickets")
    public List<TicketDTO> getTicketsByEvent( @PathVariable("id") UUID id ) {
        return null;
        //todo
    }

}

/*
GET /events → список событий
GET /events/{id} → детали события + билеты
 */