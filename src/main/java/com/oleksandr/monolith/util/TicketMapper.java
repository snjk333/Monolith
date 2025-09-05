package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketMapper {

    public List<Ticket> mapTicketsListFromDto(List<TicketDTO> tickets) {
        if (tickets == null) return List.of();
        return tickets.stream()
                .map(this::mapToEntity)
                .toList();
    }

    public Ticket mapToEntity(TicketDTO dto) {
        if (dto == null) return null;
        Ticket t = new Ticket();
        t.setId(dto.getId());
        t.setPrice(dto.getPrice());
        t.setStatus(dto.getStatus() != null ? TICKET_STATUS.valueOf(dto.getStatus()) : TICKET_STATUS.AVAILABLE);
        // Не трогаем t.setEvent(...) — это сервис сделает
        return t;
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        if (tickets == null) return List.of();
        return tickets.stream()
                .map(this::mapToDto)
                .toList();
    }

    public TicketDTO mapToDto(Ticket t) {
        if (t == null) return null;
        return TicketDTO.builder()
                .id(t.getId())
                .price(t.getPrice())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .build();
    }
}
