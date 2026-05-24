package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.sessions.GetSessionListResponseDto;
import com.techindna.eventsync.dto.sessions.GetSessionRequestDto;
import com.techindna.eventsync.dto.sessions.LinkSpeakerRequestDto;
import com.techindna.eventsync.dto.sessions.LinkSpeakerResponseDto;
import com.techindna.eventsync.dto.sessions.SessionResponseDto;
import com.techindna.eventsync.dto.speaker.SessionRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.repository.RoomRepository;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final SpeakerRepository speakerRepository;

    public SessionService(SessionRepository sessionRepository, DataValidator dataValidator, AuthService authService, RoomRepository roomRepository, EventRepository eventRepository, SpeakerRepository speakerRepository) {
        this.sessionRepository = sessionRepository;
        this.dataValidator = dataValidator;
        this.authService = authService;
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
        this.speakerRepository = speakerRepository;
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

    @Transactional
    public SessionResponseDto updateSession(String id, SessionRequestDto session) {
        dataValidator.validateUUID(id);
        dataValidator.validateSessionData(session);

        Room room = roomRepository.findRoomByName(session.getRoomName())
                .orElseThrow(() -> new NotFoundException(String.format("Room %s not found.", session.getRoomName())));
        Event event = eventRepository.findEventByTitle(session.getEventTitle())
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found.", session.getEventTitle())));

        return sessionRepository.updateSessionById(UUID.fromString(id), session, room, event)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    @Transactional
    public void deleteSessionById(String id) {
        dataValidator.validateUUID(id);
        sessionRepository.deleteSessionById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    @Transactional
    public LinkSpeakerResponseDto linkSpeakerToSession(String sessionId, String speakerId, LinkSpeakerRequestDto request) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateUUID(speakerId);
        dataValidator.checkNullData("startTime", request.getStartTime());
        dataValidator.checkNullData("endTime", request.getEndTime());

        UUID sessionUuid = UUID.fromString(sessionId);
        UUID speakerUuid = UUID.fromString(speakerId);

        sessionRepository.findSessionById(sessionUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        speakerRepository.findSpeakerById(speakerUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Speaker %s not found.", speakerId)));

        checkSpeakerLinkAuthorization(speakerUuid);

        return sessionRepository.linkSpeakerToSession(sessionUuid, speakerUuid, request.getStartTime(), request.getEndTime());
    }

    private void checkSpeakerLinkAuthorization(UUID speakerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required to link a speaker.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            return;
        }

        String currentUserId = authentication.getName();
        if (!currentUserId.equals(speakerId.toString())) {
            throw new UnauthorizedException("You are not authorized to link this speaker.");
        }
    }
}
