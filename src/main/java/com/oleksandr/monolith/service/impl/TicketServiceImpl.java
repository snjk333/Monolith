package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.repository.TicketRepository;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.service.interfaces.TicketService;
import com.oleksandr.monolith.util.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);//todo exeption
        return ticketMapper.mapToDto(ticket);
    }

    @Transactional
    @Override
    public TicketDTO updateTicketStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);//todo exeption
        ticket.setStatus(status);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.mapToDto(saved);
    }

    @Transactional
    @Override
    public TicketDTO createTicket(TicketDTO dto) {
        Ticket ticket = ticketMapper.mapToEntity(dto);
        return ticketMapper.mapToDto(ticketRepository.save(ticket));
    }


    @Transactional(readOnly = true)
    @Override
    public Ticket findEntityById(UUID ticketId) {
        return ticketRepository.findById(ticketId).orElse(null); //todo exception
    }

    @Transactional
    @Override
    public Ticket reserveTicket(UUID ticketId) {
        updateTicketStatus(ticketId, TICKET_STATUS.RESERVED);
        return findEntityById(ticketId);
    }

    @Transactional
    @Override
    public Ticket markAsSold(UUID ticketId) {
        updateTicketStatus(ticketId, TICKET_STATUS.SOLD);
        return findEntityById(ticketId);
    }

    @Transactional
    @Override
    public Ticket updateStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = findEntityById(ticketId);
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return ticket;
    }
}
