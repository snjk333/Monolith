package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Event;
import com.oleksandr.monolith.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketMapper {
    public List<Ticket> mapTicketsListFromDto(List<TicketDTO> tickets) {
        return tickets.stream()
                .map(this::mapToEntity)
                .toList();
    }

    public Ticket mapToEntity(TicketDTO ticketDTO) {
        //todo
        throw new RuntimeException("Not implemented yet");
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        return tickets.stream()
                .map(this::mapToDto)
                .toList();
    }

    public TicketDTO mapToDto(Ticket ticket) {
        //todo
        throw new RuntimeException("Not implemented yet");
    }
}