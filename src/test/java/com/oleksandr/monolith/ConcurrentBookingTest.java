package com.oleksandr.monolith;

import com.oleksandr.monolith.dto.BookingDTO;
import com.oleksandr.monolith.dto.EventDTO;
import com.oleksandr.monolith.dto.TicketDTO;
import com.oleksandr.monolith.dto.UserDTO;
import com.oleksandr.monolith.service.interfaces.BookingService;
import com.oleksandr.monolith.service.interfaces.EventService;
import com.oleksandr.monolith.service.interfaces.UserService;
import com.oleksandr.monolith.service.interfaces.AuthClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(ConcurrentBookingTest.TestConfig.class) // импортируем тестовую конфигурацию с моками
public class ConcurrentBookingTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthClientService authClientService() {
            return Mockito.mock(AuthClientService.class);
        }
    }

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthClientService authClientService; // мок из TestConfig

    @Test
    public void testConcurrentBookingViaServices_onlyServices() throws Exception {
        // 1) Создаём событие + билет через сервис (DTO). Не задаём id вручную.
        TicketDTO ticketDto = TicketDTO.builder()
                .price(200.0)
                .status("AVAILABLE")
                .build();

        EventDTO eventDto = EventDTO.builder()
                .name("Concert Test")
                .description("Test Desc")
                .location("Kyiv")
                .eventDate(LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .tickets(List.of(ticketDto))
                .build();

        EventDTO savedEvent = eventService.createEvent(eventDto);

        assertNotNull(savedEvent, "savedEvent не должен быть null");
        assertNotNull(savedEvent.getTickets(), "tickets не должны быть null");
        assertEquals(1, savedEvent.getTickets().size(), "Должен быть один билет");

        UUID ticketId = savedEvent.getTickets().get(0).getId();
        assertNotNull(ticketId, "У билета должен быть сгенерирован id");

        // 2) Подготавливаем двух пользователей и мокируем внешний вызов AuthClientService
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        UserDTO user1Dto = UserDTO.builder()
                .id(user1Id)
                .username("user1")
                .email("u1@example.com")
                .build();

        UserDTO user2Dto = UserDTO.builder()
                .id(user2Id)
                .username("user2")
                .email("u2@example.com")
                .build();

        when(authClientService.getUserById(eq(user1Id))).thenReturn(user1Dto);
        when(authClientService.getUserById(eq(user2Id))).thenReturn(user2Dto);

        // Создаём (или получаем) пользователей в БД через сервис (userService использует AuthClientService)
        userService.getOrCreateUser(user1Id);
        userService.getOrCreateUser(user2Id);

        // 3) Параллельные бронирования одного билета от двух разных пользователей
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<Boolean> task1 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                bookingService.createBooking(user1Id, ticketId);
                return true;
            } catch (Exception ex) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                bookingService.createBooking(user2Id, ticketId);
                return true;
            } catch (Exception ex) {
                return false;
            }
        };

        Future<Boolean> f1 = executor.submit(task1);
        Future<Boolean> f2 = executor.submit(task2);

        assertTrue(readyLatch.await(5, TimeUnit.SECONDS), "Потоки не подготовились за 5s");

        startLatch.countDown(); // старт одновременно

        boolean r1 = f1.get(20, TimeUnit.SECONDS);
        boolean r2 = f2.get(20, TimeUnit.SECONDS);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        int successCount = (r1 ? 1 : 0) + (r2 ? 1 : 0);
        assertEquals(1, successCount, "Должно быть ровно одно успешное бронирование");

        // 4) Проверяем через BookingService — у одного из пользователей должно быть бронирование
        List<BookingDTO> bookingsUser1 = bookingService.getBookingsByUser(user1Id);
        List<BookingDTO> bookingsUser2 = bookingService.getBookingsByUser(user2Id);

        int totalBookings = (bookingsUser1 != null ? bookingsUser1.size() : 0)
                + (bookingsUser2 != null ? bookingsUser2.size() : 0);

        assertEquals(1, totalBookings, "В сумме должно быть ровно одно бронирование");

        if (bookingsUser1 != null && bookingsUser1.size() == 1) {
            assertEquals(ticketId, bookingsUser1.get(0).getTicketId());
        } else if (bookingsUser2 != null && bookingsUser2.size() == 1) {
            assertEquals(ticketId, bookingsUser2.get(0).getTicketId());
        } else {
            fail("Ни у одного пользователя не найдено бронирования");
        }
    }
}
