package com.oleksandr.monolith.dto;

import com.oleksandr.monolith.entity.enums.TICKET_STATUS;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDTO {
    private UUID id;
    private String type;
    private double price;
    private String status;
}
