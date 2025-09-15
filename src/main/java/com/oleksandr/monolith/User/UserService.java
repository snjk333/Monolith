package com.oleksandr.monolith.User;

import com.oleksandr.monolith.Booking.BookingDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService {

    @Transactional
    User getOrCreateUser(UUID userId);
}
