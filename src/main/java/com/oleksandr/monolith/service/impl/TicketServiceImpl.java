package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.exceptions.ConcurrentUpdateException;
import com.oleksandr.monolith.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.repository.TicketRepository;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.service.interfaces.TicketService;
import com.oleksandr.monolith.util.TicketMapper;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final EventService eventService;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, EventService eventService) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.eventService = eventService;
    }

    private List<Ticket> getTicketsFromEventByID(UUID eventId) {
        return ticketRepository.findAllByEventId(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketDTO> getTicketsByEvent(UUID eventId) {
        List<Ticket> tickets = getTicketsFromEventByID(eventId);
        return ticketMapper.mapEntityListToDtoList(tickets); //todo exeption
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketDTO> getAvailableTickets(UUID eventId) {
        List<Ticket> availableTickets = getTicketsFromEventByID(eventId)
                                        .stream()
                                        .filter(
                                                ticket -> ticket.getStatus() == TICKET_STATUS.AVAILABLE
                                                )
                                        .toList();
        return ticketMapper.mapEntityListToDtoList(availableTickets); //todo exeption
    }

    @Transactional(readOnly = true)
    @Override
    public TicketDTO getTicketById(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return ticketMapper.mapToDto(ticket);
    }

    @Transactional
    @Override
    public TicketDTO updateTicketStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        ticket.setStatus(status);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.mapToDto(saved);
    }


    @Transactional
    @Override
    public TicketDTO createTicket(TicketDTO dto) {
        if (dto.getId() != null && ticketRepository.existsById(dto.getId())) {
            throw new ResourceAlreadyExistsException("Ticket with id " + dto.getId() + " already exists");
        }
        Ticket ticket = ticketMapper.mapToEntity(dto);
        return ticketMapper.mapToDto(ticketRepository.save(ticket));
    }


    @Override
    @Transactional(readOnly = true)
    public Ticket findEntityById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
    }

    @Override
    @Transactional
    public Ticket updateStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = findEntityById(ticketId);
        if (ticket.getStatus() == status) return ticket;
        ticket.setStatus(status);
        try {
            return this.save(ticket);
        } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
            throw new ConcurrentUpdateException("Ticket " + ticketId + " updated concurrently", ex);
        }
    }


    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        return ticketRepository.saveAndFlush(ticket);
    }

}
