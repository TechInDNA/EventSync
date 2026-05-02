package com.techindna.eventsync.service;

import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.validator.EventValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;

    public EventService(EventRepository eventRepository, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
    }

    public String createEvent(String title,
                              String description,
                              Instant startDate,
                              Instant endDate,
                              String location){

        Event newEvent = eventRepository.saveEvent(title, description, startDate, endDate, location);
        if (newEvent.getId() == null){
            throw new ConflictException(String.format("Event %s already exist", title));
        }
        return String.format("Event %s created with id %s", title, newEvent.getId());
    }
}
