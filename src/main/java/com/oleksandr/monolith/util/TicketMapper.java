package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketMapper {

    // DTO → Entity
    public Ticket mapToEntity(TicketDTO dto) {
        if (dto == null) return null;

        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setPrice(dto.getPrice());
        ticket.setType(dto.getType());
        ticket.setStatus(dto.getStatus() != null
                ? dto.getStatus()
                : TICKET_STATUS.AVAILABLE); // дефолтное значение
        return ticket;
    }

    public List<Ticket> mapTicketsListFromDto(List<TicketDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        List<Ticket> tickets = new ArrayList<>();
        for (TicketDTO dto : dtos) {
            Ticket t = mapToEntity(dto);
            if (t != null) tickets.add(t);
        }
        return tickets;
    }

    // Entity → DTO
    public TicketDTO mapToDto(Ticket ticket) {
        if (ticket == null) return null;

        return TicketDTO.builder()
                .id(ticket.getId())
                .type(ticket.getType())
                .price(ticket.getPrice())
                .status(ticket.getStatus() != null ? ticket.getStatus() : null)
                .build();
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) return List.of();
        List<TicketDTO> dtos = new ArrayList<>();
        for (Ticket t : tickets) {
            TicketDTO dto = mapToDto(t);
            if (dto != null) dtos.add(dto);
        }
        return dtos;
    }
}
