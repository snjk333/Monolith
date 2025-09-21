package com.oleksandr.monolith.Ticket.DTO;

import com.oleksandr.monolith.Ticket.EntityRepo.TICKET_STATUS;
import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDTO {

    private UUID id;

    private UUID eventId;

    @NotBlank
    private String type;

    @Min(0)
    private double price;

    @NotNull
    private TICKET_STATUS status;
}
