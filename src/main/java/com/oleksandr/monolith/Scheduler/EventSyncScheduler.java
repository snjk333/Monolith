package com.oleksandr.monolith.Scheduler;

import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.integration.wrapper.EventSyncService;
import com.oleksandr.monolith.integration.wrapper.WrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSyncScheduler {

    private final WrapperService wrapperService;
    private final EventSyncService eventSyncService;


    @Scheduled(fixedDelayString = "${scheduler.delay:120000}")
    public void syncEventsSingleCall() {
        List<EventDTO> all = wrapperService.fetchExternalEvents(); // one call, includeTickets=true
        if (all == null || all.isEmpty()) return;
        eventSyncService.syncAll(all);
    }
}
