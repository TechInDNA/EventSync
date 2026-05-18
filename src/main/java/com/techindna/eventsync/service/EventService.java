package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.events.EventRequestDto;
import com.techindna.eventsync.dto.events.EventResponseDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final DataValidator dataValidator;

    public EventService(EventRepository eventRepository, DataValidator dataValidator) {
        this.eventRepository = eventRepository;
        this.dataValidator = dataValidator;
    }

    public EventResponseDto createEvent(EventRequestDto request){
        dataValidator.validateEventData(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getLocation()
        );

        return eventRepository.saveEvent(request)
                .orElseThrow(() -> new ConflictException(String.format("Event %s already exist.", request.getTitle())));
    }

    public List<Event> getAllEvents(PaginationRequestDto pagination) {
        return eventRepository.getAllEvents(pagination.getOffset(), pagination.getLimit());
    }

    public int countEvents() {
        return eventRepository.countEvents();
    }

    public Event updateEvent(UUID id, String title, String description, Instant startDate, Instant endDate, String location) {
        Optional<Event> existing = eventRepository.findEventByTitle(title);

        if (existing.isPresent()) {
            Event e = existing.get();
            throw new ConflictException(String.format(
                    "An event with title '%s' already exists (ID: %s, Location: %s, Creation date: %s)",
                    e.getTitle(), e.getId(), e.getLocation(), e.getCreatedAt()));
        }

        return eventRepository.updateEvent(id, title, description, startDate, endDate, location);
    }

    public UUID deleteEventById(UUID id) {
        return eventRepository.deleteEventById(id);
    }
}
