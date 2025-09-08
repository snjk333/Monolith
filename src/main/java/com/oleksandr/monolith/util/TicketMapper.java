package com.oleksandr.monolith.util;

import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.entity.Ticket;
import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketMapper {

    private static final Logger log = LoggerFactory.getLogger(TicketMapper.class);

    public List<Ticket> mapTicketsListFromDto(List<TicketDTO> tickets) {
        if (tickets == null) {
            log.warn("Received null TicketDTO list, returning empty list");
            return List.of();
        }
        log.info("Mapping list of {} TicketDTOs to Ticket entities", tickets.size());
        List<Ticket> entities = tickets.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toCollection(ArrayList::new));
        log.info("Mapped {} Ticket entities successfully", entities.size());
        return entities;
    }

    public Ticket mapToEntity(TicketDTO dto) {
        if (dto == null) {
            log.warn("Received null TicketDTO, returning null Ticket entity");
            return null;
        }
        Ticket t = new Ticket();
        t.setId(dto.getId());
        t.setPrice(dto.getPrice());
        t.setStatus(dto.getStatus() != null ? TICKET_STATUS.valueOf(dto.getStatus()) : TICKET_STATUS.AVAILABLE);
        log.info("Mapped TicketDTO (id={}) to Ticket entity successfully", dto.getId());
        return t;
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        if (tickets == null) {
            log.warn("Received null Ticket list, returning empty list");
            return List.of();
        }
        log.info("Mapping list of {} Ticket entities to TicketDTOs", tickets.size());
        List<TicketDTO> dtos = tickets.stream()
                .map(this::mapToDto)
                .toList();
        log.info("Mapped {} TicketDTOs successfully", dtos.size());
        return dtos;
    }

    public TicketDTO mapToDto(Ticket t) {
        if (t == null) {
            log.warn("Received null Ticket entity, returning null TicketDTO");
            return null;
        }
        TicketDTO dto = TicketDTO.builder()
                .id(t.getId())
                .price(t.getPrice())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .build();
        log.info("Mapped Ticket entity (id={}) to TicketDTO successfully", t.getId());
        return dto;
    }
}
