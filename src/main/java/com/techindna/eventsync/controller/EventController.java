package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetEventListResponseDto;
import com.techindna.eventsync.dto.PostEventRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.EventService;
import com.techindna.eventsync.validator.EventValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventController(EventService eventService, EventValidator eventValidator){
        this.eventService = eventService;
        this.eventValidator = eventValidator;
    }

    @GetMapping
    public ResponseEntity<GetEventListResponseDto> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            GetEventListResponseDto response = new GetEventListResponseDto(events, events.size());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody PostEventRequestDto request) {
        try {
            eventValidator.validateEventData(
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getLocation()
            );

            Event newEvent = eventService.createEvent(
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getLocation()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
