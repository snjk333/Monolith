package com.oleksandr.monolith.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {
    private UUID id;
    private UUID ticketId;
    private UUID userId;
    private String status;
}
