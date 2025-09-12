package com.oleksandr.monolith.Ticket;

import com.oleksandr.monolith.common.exceptions.ConcurrentUpdateException;
import com.oleksandr.monolith.common.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    @Override
    public Ticket createTicket(Ticket ticket) {
        if (ticket.getId() != null && ticketRepository.existsById(ticket.getId())) {
            throw new ResourceAlreadyExistsException("Ticket with id " + ticket.getId() + " already exists");
        }
        return ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    @Override
    public Ticket updateStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getStatus() != status) {
            ticket.setStatus(status);
            try {
                return ticketRepository.saveAndFlush(ticket);
            } catch (OptimisticLockingFailureException ex) {
                throw new ConcurrentUpdateException("Ticket " + ticketId + " updated concurrently", ex);
            }
        }
        return ticket;
    }

    @Transactional(readOnly = true)
    @Override
    public Ticket findById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Ticket> findTicketsByEventId(UUID eventId) {
        return ticketRepository.findAllByEventId(eventId);
    }
}
