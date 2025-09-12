package com.oleksandr.monolith.Ticket;

import com.oleksandr.monolith.common.exceptions.ConcurrentUpdateException;
import com.oleksandr.monolith.common.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.Event.EventService;
import jakarta.persistence.OptimisticLockException;
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
    private final EventService eventService;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, EventService eventService) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.eventService = eventService;
    }

    private List<Ticket> getTicketsFromEventByID(UUID eventId) {
        log.info("Fetching all tickets for event ID: {}", eventId);
        List<Ticket> tickets = ticketRepository.findAllByEventId(eventId);
        log.info("Fetched {} tickets for event ID: {}", tickets.size(), eventId);
        return tickets;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketDTO> getTicketsByEvent(UUID eventId) {
        List<Ticket> tickets = getTicketsFromEventByID(eventId);
        return ticketMapper.mapEntityListToDtoList(tickets);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketDTO> getAvailableTickets(UUID eventId) {
        List<Ticket> availableTickets = getTicketsFromEventByID(eventId).stream()
                .filter(ticket -> ticket.getStatus() == TICKET_STATUS.AVAILABLE)
                .toList();
        log.info("Fetched {} available tickets for event ID: {}", availableTickets.size(), eventId);
        return ticketMapper.mapEntityListToDtoList(availableTickets);
    }

    @Transactional(readOnly = true)
    @Override
    public TicketDTO getTicketById(UUID ticketId) {
        log.info("Fetching ticket by ID: {}", ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Ticket not found with ID: " + ticketId);
                });
        log.info("Ticket found: {}", ticket.getId());
        return ticketMapper.mapToDto(ticket);
    }

    @Transactional
    @Override
    public TicketDTO updateTicketStatus(UUID ticketId, TICKET_STATUS status) {
        log.info("Updating ticket status for ID {} to {}", ticketId, status);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Ticket not found with ID: " + ticketId);
                });
        ticket.setStatus(status);
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        log.info("Ticket status updated for ID {} to {}", ticketId, saved.getStatus());
        return ticketMapper.mapToDto(saved);
    }

    @Transactional
    @Override
    public TicketDTO createTicket(TicketDTO dto) {
        log.info("Creating ticket with ID: {}", dto.getId());
        if (dto.getId() != null && ticketRepository.existsById(dto.getId())) {
            log.warn("Ticket creation failed: Ticket with ID {} already exists", dto.getId());
            throw new ResourceAlreadyExistsException("Ticket with id " + dto.getId() + " already exists");
        }
        Ticket ticket = ticketMapper.mapToEntity(dto);
        ticket.setEvent(eventService.findById(dto.getEventId()));
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        log.info("Ticket created successfully with ID: {}", saved.getId());
        return ticketMapper.mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findEntityById(UUID ticketId) {
        log.info("Finding ticket entity by ID: {}", ticketId);
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Ticket not found: " + ticketId);
                });
    }

    @Override
    @Transactional
    public Ticket updateStatus(UUID ticketId, TICKET_STATUS status) {
        log.info("Updating ticket entity status for ID {} to {}", ticketId, status);
        Ticket ticket = findEntityById(ticketId);
        if (ticket.getStatus() == status) {
            log.info("Ticket ID {} already has status {}", ticketId, status);
            return ticket;
        }
        ticket.setStatus(status);
        try {
            Ticket saved = this.save(ticket);
            log.info("Ticket ID {} status updated to {}", ticketId, saved.getStatus());
            return saved;
        } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
            log.error("Concurrent update detected for ticket ID {}", ticketId, ex);
            throw new ConcurrentUpdateException("Ticket " + ticketId + " updated concurrently", ex);
        }
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        log.info("Saving ticket entity ID: {}", ticket.getId());
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        log.info("Ticket saved successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public List<Ticket> findTickets(List<Ticket> tickets) {
        return tickets;
//        if (tickets != null) return tickets;
//        else return //todo make new tickets
    }
}
