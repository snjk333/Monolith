package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.entity.Booking;
import com.oleksandr.monolith.entity.User;
import com.oleksandr.monolith.repository.UserRepository;
import com.oleksandr.monolith.service.interfaces.AuthClientService;
import com.oleksandr.monolith.service.interfaces.UserService;
import com.oleksandr.monolith.util.BookingMapper;
import com.oleksandr.monolith.util.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

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
        User user = getOrCreateUser(userId);
        List<Booking> bookings = user.getBookings();
        return bookingMapper.mapEntityListToDtoList(bookings);
    }

    @Transactional
    @Override
    public User getOrCreateUser(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            UserDTO userDTO = authClientService.getUserById(userId); //todo exception
            user = userMapper.mapToEntity(userDTO);
            userRepository.save(user);
        }
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserDto(UUID userId) {
        User user = getOrCreateUser(userId);
        return userMapper.mapToDto(user);
    }

    @Transactional
    @Override
    public UserDTO updateUser(UserDTO dto) {
        return null;
    }
}
