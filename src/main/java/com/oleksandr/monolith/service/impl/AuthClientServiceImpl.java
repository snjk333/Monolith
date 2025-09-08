package com.oleksandr.monolith.service.impl;

import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.service.interfaces.AuthClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Slf4j
@Service
public class AuthClientServiceImpl implements AuthClientService {


    //todo Доработать ВЕСЬ КЛАСС
    private final WebClient.Builder webClientBuilder;

    public AuthClientServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient authClient() {
        return webClientBuilder
                .baseUrl("http://auth-service:8080/api/users")
                .build();
    }
    @Override
    public UserDTO getUserById(UUID userId) {
        try {
            // Build a WebClient instance with Auth MS base URL
            WebClient webClient = webClientBuilder
                    .baseUrl("http://auth-service:8080/api/users") // <- Change to your Auth MS URL
                    .build();

            // Send synchronous GET request to Auth MS
            return webClient
                    .get()
                    .uri("/{id}", userId) // Append userId to the URL path
                    .retrieve()
                    .bodyToMono(UserDTO.class) // Convert JSON response to UserDTO
                    .block(); // Block until the response is received (synchronous)

        } catch (WebClientResponseException.NotFound e) {
            // Auth MS responded with 404 → User not found
            log.warn("User {} not found in Auth MS", userId);
            return null;

        } catch (Exception e) {
            // Any other error: network issues, Auth MS unavailable, etc.
            log.error("Failed to fetch user {} from Auth MS: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl("http://auth-service:8080/api/users")
                    .build();

            return webClient
                    .patch()
                    .uri("/{id}", userDto.getId()) // PATCH /api/users/{id}
                    .bodyValue(userDto)            // Send JSON body
                    .retrieve()
                    .bodyToMono(UserDTO.class)     // Expect updated UserDTO
                    .block();                      // Sync call for monolith

        } catch (WebClientResponseException.NotFound e) {
            log.warn("User {} not found in Auth MS while updating", userDto.getId());
            return null;
        } catch (Exception e) {
            log.error("Failed to update user {} in Auth MS: {}", userDto.getId(), e.getMessage());
            return null;
        }
    }
}

//   @Override
//    public UserDTO getUserById(UUID userId) {
//        try {
//            log.debug("Отправляем GET /api/users/{} в Auth MS", userId);
//
//            return authClient()
//                    .get()
//                    .uri("/{id}", userId)
//                    .retrieve()
//                    .bodyToMono(UserDTO.class)
//                    .block(); // синхронный вызов для монолита
//
//        } catch (WebClientResponseException.NotFound e) {
//            log.warn("Пользователь {} не найден в Auth MS", userId);
//            return null;
//        } catch (Exception e) {
//            log.error("Ошибка при запросе к Auth MS: {}", e.getMessage(), e);
//            return null;
//        }
//    }