package com.oleksandr.monolith.dto;

import com.oleksandr.monolith.entity.enums.Role;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Role role;

    private List<BookingDTO> bookings;
}
