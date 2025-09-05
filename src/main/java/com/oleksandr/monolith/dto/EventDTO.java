package com.oleksandr.monolith.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private UUID id;
    private String name;
    private String description;
    private String location;
    private String imageURL;
    private String eventDate;
    private List<TicketDTO> tickets;
}
