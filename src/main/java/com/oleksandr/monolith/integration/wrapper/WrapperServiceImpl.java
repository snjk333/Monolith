package com.oleksandr.monolith.integration.wrapper;

import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WrapperServiceImpl implements WrapperService {

    private final WebClient webClient;

    public WrapperServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081/external").build();
    }

    /**
     * Fetch all external events. If includeTickets == true, external service
     * should return events with tickets embedded (single request).
     */
    @Override
    public List<EventDTO> fetchExternalEvents() {
        // variant: single call with tickets
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/events")
                        .queryParam("includeTickets", "true")
                        .build())
                .retrieve()
                .bodyToFlux(EventDTO.class)
                .collectList()
                .block();
    }

    @Override
    public EventDTO fetchEventById(java.util.UUID eventId) {
        return webClient.get()
                .uri("/events/{id}?includeTickets=true", eventId)
                .retrieve()
                .bodyToMono(EventDTO.class)
                .block();
    }

    @Override
    public List<TicketDTO> fetchTicketsByEvent(java.util.UUID eventId) {
        return webClient.get()
                .uri("/events/{id}/tickets", eventId)
                .retrieve()
                .bodyToFlux(TicketDTO.class)
                .collectList()
                .block();
    }
}
