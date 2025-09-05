package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import com.oleksandr.monolith.repository.EventRepository;
import com.oleksandr.monolith.repository.TicketRepository;
import com.oleksandr.monolith.service.interfaces.tmp_withRealization.TicketService;
import com.oleksandr.monolith.util.TicketMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, EventRepository eventRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.ticketMapper = ticketMapper;
    }

    private List<Ticket> getTicketsFromEventByID(UUID eventID) {
        Event event = eventRepository.findById(eventID).orElse(null);//todo exeption
        return event.getTickets();
    }

    @Override
    public List<TicketDTO> getTicketsByEvent(UUID eventId) {
        List<Ticket> tickets = getTicketsFromEventByID(eventId);
        return ticketMapper.mapEntityListToDtoList(tickets); //todo exeption
    }

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

    @Override
    public TicketDTO getTicketById(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);//todo exeption
        return ticketMapper.mapToDto(ticket);
    }

    @Override
    public TicketDTO updateTicketStatus(UUID ticketId, TICKET_STATUS status) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);//todo exeption
        ticket.setStatus(status);
        return ticketMapper.mapToDto(ticket);
    }

    @Override
    public TicketDTO createTicket(TicketDTO dto) {
        Ticket ticket = ticketMapper.mapToEntity(dto);
        return ticketMapper.mapToDto(ticketRepository.save(ticket));
    }
}
