package com.oleksandr.monolith.service.interfaces.tmp_withRealization;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    // Основное
    List<TicketDTO> getTicketsByEvent(UUID eventId);
    List<TicketDTO> getAvailableTickets(UUID eventId);
    TicketDTO getTicketById(UUID ticketId);

    // Внутренние операции
    TicketDTO updateTicketStatus(UUID ticketId, TICKET_STATUS status);

    // CRUD для админки (позже)
    TicketDTO createTicket(TicketDTO dto);
}
