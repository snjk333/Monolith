package com.oleksandr.monolith.Event;

import com.oleksandr.monolith.Ticket.Ticket;
import com.oleksandr.monolith.common.exceptions.ResourceAlreadyExistsException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.Ticket.TicketService;
import com.oleksandr.monolith.Ticket.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final TicketService ticketService;
    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public EventServiceImpl(EventRepository eventRepository, TicketService ticketService,
                            EventMapper eventMapper, TicketMapper ticketMapper) {
        this.eventRepository = eventRepository;
        this.ticketService = ticketService;
        this.eventMapper = eventMapper;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    @Override
    public EventDTO createEvent(EventDTO dto) {
        log.info("Creating new event with ID: {}", dto.getId());

        if (dto.getId() != null && eventRepository.existsById(dto.getId())) {
            log.warn("Event creation failed: Event with ID {} already exists", dto.getId());
            throw new ResourceAlreadyExistsException("Event with id " + dto.getId() + " already exists");
        }

        Event event = eventMapper.mapToEntity(dto);

        // Устанавливаем Event в тикетах и создаём новые тикеты
        if (dto.getTickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            tickets.forEach(t -> t.setEvent(event));
            event.setTickets(tickets);
        } else {
            event.setTickets(new ArrayList<>());
        }

        Event saved = eventRepository.saveAndFlush(event);
        log.info("Event created successfully with ID: {}", saved.getId());
        return eventMapper.mapToDto(saved);
    }

    @Transactional
    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO dto) {
        log.info("Updating event with ID: {}", eventId);

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        // Обновляем основные поля через mapper
        Event updatedEvent = eventMapper.updateEventInformation(existingEvent, dto);

        // --- reconcile тикетов ---
        if (dto.getTickets() != null) {
            List<Ticket> incomingTickets = ticketMapper.mapTicketsListFromDto(dto.getTickets());
            Map<UUID, Ticket> existingMap = updatedEvent.getTickets().stream()
                    .filter(t -> t.getId() != null)
                    .collect(Collectors.toMap(Ticket::getId, t -> t));

            List<Ticket> finalTickets = new ArrayList<>();

            for (Ticket t : incomingTickets) {
                if (t.getId() == null) {
                    // новый тикет
                    t.setEvent(updatedEvent);
                    finalTickets.add(t);
                } else if (existingMap.containsKey(t.getId())) {
                    // обновляем существующий
                    Ticket existing = existingMap.get(t.getId());
                    existing.setPrice(t.getPrice());
                    existing.setType(t.getType());
                    existing.setStatus(t.getStatus() != null ? t.getStatus() : existing.getStatus());
                    finalTickets.add(existing);
                    existingMap.remove(t.getId());
                } else {
                    // пришёл тикет с id, которого нет в базе → добавляем
                    t.setEvent(updatedEvent);
                    finalTickets.add(t);
                }
            }

            // удаляем все тикеты, которые не пришли в патче
            // orphanRemoval=true обеспечит удаление из БД
            updatedEvent.getTickets().clear();
            updatedEvent.getTickets().addAll(finalTickets);
        }

        Event saved = eventRepository.saveAndFlush(updatedEvent);
        log.info("Event updated successfully with ID: {}", saved.getId());
        return eventMapper.mapToDto(saved);
    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {
        log.info("Deleting event with ID: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }
        eventRepository.deleteById(eventId);
        log.info("Event deleted successfully with ID: {}", eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public EventDTO getEventDTOById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        return eventMapper.mapToDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.mapListToDtoList(events);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> getUpcomingEvents() {
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return eventMapper.mapListToDtoList(events);
    }

    @Transactional(readOnly = true)
    @Override
    public Event findById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
    }
}
