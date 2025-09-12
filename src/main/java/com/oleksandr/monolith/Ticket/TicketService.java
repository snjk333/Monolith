package com.oleksandr.monolith.Ticket;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    @Transactional
    Ticket createTicket(Ticket ticket);

    @Transactional
    Ticket updateStatus(UUID ticketId, TICKET_STATUS status);

    @Transactional(readOnly = true)
    Ticket findById(UUID ticketId);

    @Transactional(readOnly = true)
    List<Ticket> findTicketsByEventId(UUID eventId);
}
