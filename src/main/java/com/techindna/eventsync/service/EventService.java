package com.techindna.eventsync.service;

import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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

    public List<Event> getAllEvents() {
        return eventRepository.findAllEvents();
    }
}
