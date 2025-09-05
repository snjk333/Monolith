package com.oleksandr.monolith.dto;

import com.oleksandr.monolith.entity.enums.Role;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private Role role;
    private List<BookingDTO> bookings;
}
