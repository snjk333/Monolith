package com.oleksandr.monolith.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String imageURL;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;


}
