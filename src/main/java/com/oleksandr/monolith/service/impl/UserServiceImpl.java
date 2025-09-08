package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.User;
import com.oleksandr.monolith.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.repository.UserRepository;
import com.oleksandr.monolith.service.interfaces.AuthClientService;
import com.oleksandr.monolith.service.interfaces.UserService;
import com.oleksandr.monolith.util.BookingMapper;
import com.oleksandr.monolith.util.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AuthClientService authClientService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    public UserServiceImpl(AuthClientService authClientService, UserRepository userRepository, UserMapper userMapper, BookingMapper bookingMapper) {
        this.authClientService = authClientService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bookingMapper = bookingMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDTO> getUserBookings(UUID userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        User user = getOrCreateUser(userId);
        List<Booking> bookings = user.getBookings();
        log.info("User ID {} has {} bookings", userId, bookings.size());
        return bookingMapper.mapEntityListToDtoList(bookings);
    }

    @Transactional
    @Override
    public User getOrCreateUser(UUID userId) {
        log.info("Looking for user with ID: {}", userId);
        return userRepository.findById(userId).orElseGet(() -> {
            log.info("User not found locally, fetching from Auth service: {}", userId);
            UserDTO userDTO = authClientService.getUserById(userId);
            User user = userMapper.mapToEntity(userDTO);
            try {
                User savedUser = userRepository.saveAndFlush(user);
                log.info("User created locally with ID: {}", savedUser.getId());
                return savedUser;
            } catch (DataIntegrityViolationException e) {
                log.warn("Race condition detected while creating user ID: {}. Fetching existing user.", userId);
                return userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("User not found after race condition for ID: {}", userId);
                            return new ResourceNotFoundException("User not found after race condition");
                        });
            }
        });
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserDto(UUID userId) {
        log.info("Fetching UserDTO for user ID: {}", userId);
        User user = getOrCreateUser(userId);
        return userMapper.mapToDto(user);
    }

    @Transactional
    @Override
    public UserDTO updateUserInfo(UserDTO dto) {
        log.info("Updating user info for ID: {}", dto.getId());
        User userToChange = this.getOrCreateUser(dto.getId());
        User updatedUser = userMapper.updateUserInformation(userToChange, dto);
        User savedUser = userRepository.save(updatedUser);
        log.info("User info updated successfully for ID: {}", savedUser.getId());
        return userMapper.mapToDto(savedUser);
    }
}
