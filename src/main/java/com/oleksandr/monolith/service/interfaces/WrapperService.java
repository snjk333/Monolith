package com.oleksandr.monolith.service.interfaces;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.dto.TicketDTO;

import java.util.List;
import java.util.UUID;

public interface WrapperService {

    List<EventDTO> fetchExternalEvents();
    EventDTO fetchEventById(UUID eventId);
    List<TicketDTO> fetchTicketsByEvent(UUID eventId);
}
