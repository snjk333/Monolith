package com.oleksandr.monolith.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingsController {
}

/*
POST /bookings → забронировать билет
GET /bookings/{id} → детали бронирования
GET /users/{id}/bookings → список бронирований пользователя
PUT /bookings/{id}/cancel → отменить бронирование
PUT /bookings/{id}/confirm → подтвердить оплату (например, после транзакции)
 */