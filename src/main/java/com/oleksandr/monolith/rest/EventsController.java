package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Coordinator.EventTicketCoordinator;
import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.Service.EventService;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventsController {

    private final EventService eventService;
    private final EventTicketCoordinator eventTicketCoordinator;

    public EventsController(EventService eventService, EventTicketCoordinator eventTicketCoordinator) {
        this.eventService = eventService;
        this.eventTicketCoordinator = eventTicketCoordinator;
    }

    @GetMapping
    public List<EventSummaryDTO> getAllEvents()
    {
        return eventService.getAllEventsSummary();
    }

    @GetMapping("/{id}")
    public EventDetailsDTO getEventDetails(@PathVariable("id") UUID id) {
        return eventService.getEventDetails(id);
    }

    @GetMapping("/{id}/tickets")
    public List<TicketDTO> getTicketsByEvent( @PathVariable("id") UUID id ) {
        return eventTicketCoordinator.getTicketsByEventId(id);
    }

}
