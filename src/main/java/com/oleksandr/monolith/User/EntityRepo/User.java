package com.oleksandr.monolith.User.EntityRepo;

import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    //@GeneratedValue(generator = "UUID")
    private UUID id;

    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;
}
