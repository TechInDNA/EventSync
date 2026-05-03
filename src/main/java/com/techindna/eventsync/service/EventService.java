package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public Event createEvent(String title,
                               String description,
                               Instant startDate,
                               Instant endDate,
                               String location){

        Event newEvent = eventRepository.saveEvent(title, description, startDate, endDate, location);
        if (newEvent.getId() == null){
            throw new ConflictException(String.format("Event %s already exist", title));
        }
        return newEvent;
    }

    public List<Event> getAllEvents(PaginationRequestDto pagination) {
        return eventRepository.findAllEvents(pagination.getOffset(), pagination.getLimit());
    }

    public int countEvents() {
        return eventRepository.countEvents();
    }

    public Event updateEvent(UUID id, String title, String description, Instant startDate, Instant endDate, String location) {
        Event eventExist = eventRepository.findEventByTitle(title);

        if (eventExist.getId() != null){
            throw new ConflictException(String.format("An event with title '%s' already exists (ID: %s, Location: %s, Creation date: %s)",
                    eventExist.getTitle(), eventExist.getId(), eventExist.getLocation(), eventExist.getCreatedAt()));
        }

        return eventRepository.updateEvent(id, title, description, startDate, endDate, location);
    }

    public UUID deleteEvent(UUID id) {
        return eventRepository.deleteEvent(id);
    }
}
