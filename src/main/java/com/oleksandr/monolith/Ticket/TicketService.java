package com.oleksandr.monolith.Ticket;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    @Transactional
    Ticket reserveTicket(UUID ticketId);

    @Transactional
    void markAvailable(Ticket ticket);

    @Transactional
    void markSold(Ticket ticket);

    @Transactional(readOnly = true)
    Ticket findById(UUID ticketId);

    @Transactional(readOnly = true)
    boolean isTicketAvailable(UUID ticketId);
}
