package com.oleksandr.monolith.integration.wrapper;

import com.oleksandr.monolith.Event.EventDTO;
import com.oleksandr.monolith.Ticket.TicketDTO;

import java.util.List;
import java.util.UUID;

public interface WrapperService {

    List<EventDTO> fetchExternalEvents();
    EventDTO fetchEventById(UUID eventId);
    List<TicketDTO> fetchTicketsByEvent(UUID eventId);
}
