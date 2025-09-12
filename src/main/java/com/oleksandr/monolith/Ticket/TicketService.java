package com.oleksandr.monolith.Ticket;

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

    Ticket findEntityById(UUID ticketId);        // throws if not found

    Ticket updateStatus(UUID ticketId, TICKET_STATUS status); // general

    Ticket save(Ticket ticket);

    List<Ticket> findTickets(List<Ticket> tickets);
}
