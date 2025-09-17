package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Event.EventDTO;
import com.oleksandr.monolith.Ticket.TicketDTO;
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
    public EventDTO getEventByUUID(
            @PathVariable("id") UUID id,
            @RequestParam(value = "includeTickets", defaultValue = "false") boolean includeTickets
    ) {
        return null;
        //todo
    }

    @GetMapping("/{id}/tickets")
    public List<TicketDTO> getTicketsByEvent(
            @PathVariable("id") UUID id
    ) {
        return null;
        //todo
    }

}

/*
GET /events → список событий
GET /events/{id} → детали события + билеты
 */