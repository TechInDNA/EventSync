package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.sessions.GetSessionListResponseDto;
import com.techindna.eventsync.dto.sessions.GetSessionRequestDto;
import com.techindna.eventsync.dto.sessions.SessionResponseDto;
import com.techindna.eventsync.dto.speaker.SessionRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.repository.RoomRepository;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final DataValidator dataValidator;
    private final AuthService authService;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;

    public SessionService(SessionRepository sessionRepository, DataValidator dataValidator, AuthService authService, RoomRepository roomRepository, EventRepository eventRepository) {
        this.sessionRepository = sessionRepository;
        this.dataValidator = dataValidator;
        this.authService = authService;
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public SessionResponseDto createSession(SessionRequestDto sessionRequestDto) {
        dataValidator.validateSessionData(sessionRequestDto);

        Room room = roomRepository.findRoomByName(sessionRequestDto.getRoomName())
                .orElseThrow(() -> new NotFoundException("Room not found."));
        Event event = eventRepository.findEventByTitle(sessionRequestDto.getEventTitle())
                .orElseThrow(() -> new NotFoundException("Event not found."));

        return sessionRepository.createSession(sessionRequestDto, room, event)
                .orElseThrow(() -> new ConflictException(String.format("Session with title '%s' already exists", sessionRequestDto.getTitle())));
    }

    @Transactional(readOnly = true)
    public GetSessionListResponseDto getAllSessions(GetSessionRequestDto request, String page, String size, String ipAddress){
        authService.checkClient(ipAddress);
        dataValidator.validatePageAndSize(page, size);
        dataValidator.validateSessionRequestData(request);

        PaginationRequestDto pagination = new PaginationRequestDto(Integer.parseInt(page), Integer.parseInt(size));
        return new GetSessionListResponseDto(
                sessionRepository.getAllSessions(request, pagination),
                sessionRepository.countFilteredSessions(request),
                pagination.getPage(),
                pagination.getSize()
        );
    }

    public SessionResponseDto updateSession(UUID id, SessionRequestDto session) {
        dataValidator.validateSessionData(session);
        if (sessionRepository.findSessionByTitleExcludingId(session.getTitle(), id).isPresent()){
            throw new ConflictException(String.format("Session with title '%s' already exists.", session.getTitle()));
        }
        return sessionRepository.updateSessionById(id, session)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    public void deleteSessionById(String id) {
        dataValidator.validateUUID(id);
        sessionRepository.deleteSessionById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }
}
