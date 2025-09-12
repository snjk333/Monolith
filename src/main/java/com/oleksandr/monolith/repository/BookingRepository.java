package com.oleksandr.monolith.repository;

import com.oleksandr.monolith.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByUserId(UUID userId);

    Optional<Booking> findByUserIdAndTicketId(UUID id, UUID id1);
}
