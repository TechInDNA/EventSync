package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.events.*;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.mapper.EventMapper;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    private final DataSource dataSource;
    private final DataValidator dataValidator;
    private final AuthService authService;

    public EventService(EventRepository eventRepository, SessionRepository sessionRepository,
                        DataSource dataSource, DataValidator dataValidator, AuthService authService) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.dataSource = dataSource;
        this.dataValidator = dataValidator;
        this.authService = authService;
    }

    public EventResponseDto createEvent(EventRequestDto request){
        dataValidator.validateEventData(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getLocation()
        );

        try (Connection connection = dataSource.getConnection()){
            return eventRepository.saveEvent(request, connection)
                    .orElseThrow(() -> new ConflictException(String.format("Event %s already exist.", request.getTitle())));
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public GetEventListResponseDto getAllEvents(String page, String size, String title, String location, String ipAddress) {
        authService.checkClient(ipAddress);
        dataValidator.validatePageAndSize(page, size);

        PaginationRequestDto pagination = new PaginationRequestDto(Integer.parseInt(page), Integer.parseInt(size));
        List<Event> events = eventRepository
                .getAllEvents(pagination.getOffset(), pagination.getLimit(), title, location);

        return new GetEventListResponseDto(
                events,
                eventRepository.countEvents(title, location),
                pagination.getPage(),
                pagination.getSize()
        );
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventById(String id, String ipAddress) {
        authService.checkClient(ipAddress);
        dataValidator.validateUUID(id);

        Event event = eventRepository.findEventById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found.", id)));

        EventResponseDto response = EventMapper.mapEventToResponseDto(event);
        List<EventSessionResponseDto> sessions = sessionRepository.findSessionsByEventId(UUID.fromString(id));
        response.setSessions(sessions.isEmpty() ? null : sessions);

        return response;
    }

    public EventResponseDto updateEventById(String id, PutEventRequestDto request) {
        dataValidator.validateUUID(id);
        dataValidator.validateEventData(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getLocation()
        );

        try (Connection connection = dataSource.getConnection()) {
            Optional<Event> existing = eventRepository.findEventByTitle(connection, request.getTitle());

            if (existing.isPresent()) {
                Event e = existing.get();
                throw new ConflictException(String.format(
                        "An event with title '%s' already exists (ID: %s, Location: %s, Creation date: %s)",
                        e.getTitle(), e.getId(), e.getLocation(), e.getCreatedAt()));
            }

            EventRequestDto eventRequest = EventMapper.mapPutRequestToRequestDto(request, id);
            EventResponseDto response = eventRepository.updateEvent(connection, eventRequest)
                    .orElseThrow(() -> new NotFoundException(String.format("Event %s not found.", id)));

            List<EventSessionResponseDto> sessions = sessionRepository.findSessionsByEventId(UUID.fromString(id));
            response.setSessions(sessions);

            return response;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public UUID deleteEventById(String id) {
        dataValidator.validateUUID(id);
        try (Connection connection = dataSource.getConnection()){
            return eventRepository.deleteEventById(UUID.fromString(id), connection)
                    .orElseThrow(() -> new NotFoundException(String.format("Event %s not found.", id)));
        } catch (SQLException e){
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
