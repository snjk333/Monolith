package com.oleksandr.monolith.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
}


/*
GET /users/{id} → получить профиль пользователя
PATCH /users/{id} → обновить профиль
 */