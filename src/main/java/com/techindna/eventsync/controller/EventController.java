package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.events.EventRequestDto;
import com.techindna.eventsync.dto.events.PutEventRequestDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {
  private final EventService eventService;

  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  @GetMapping
  public ResponseEntity<?> getAllEvents(
      @RequestParam(required = false, defaultValue = "1") String page,
      @RequestParam(required = false, defaultValue = "10") String size,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String location,
      HttpServletRequest servletRequest) {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(
              eventService.getAllEvents(
                  page, size, title, location, servletRequest.getRemoteAddr()));
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (InternalServerErrorException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred, please try again later");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getEventById(
      @PathVariable String id, HttpServletRequest servletRequest) {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(eventService.getEventById(id, servletRequest.getRemoteAddr()));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (InternalServerErrorException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred, please try again later");
    }
  }

  @PostMapping
  public ResponseEntity<?> createEvent(@RequestBody EventRequestDto request) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (ConflictException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (InternalServerErrorException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred, please try again later");
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateEvent(
      @PathVariable String id, @RequestBody PutEventRequestDto request) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventById(id, request));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (ConflictException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (InternalServerErrorException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred, please try again later");
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEvent(@PathVariable(required = false) String id) {
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(String.format("Event %s deleted.", eventService.deleteEventById(id)));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (InternalServerErrorException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred, please try again later");
    }
  }
}
