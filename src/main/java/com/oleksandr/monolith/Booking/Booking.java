package com.oleksandr.monolith.Booking;

import com.oleksandr.monolith.Ticket.Ticket;
import com.oleksandr.monolith.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings",
        uniqueConstraints = @UniqueConstraint(name = "uk_bookings_ticket", columnNames = {"ticket_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Booking {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private LocalDateTime createdAt;
    private boolean paid;

    @Enumerated(EnumType.STRING)
    private BOOKING_STATUS status;

    @Version
    @Column(nullable = false)
    private Long version;
}
