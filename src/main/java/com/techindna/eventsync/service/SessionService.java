package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final DataValidator dataValidator;

    public SessionService(SessionRepository sessionRepository, DataValidator dataValidator) {
        this.sessionRepository = sessionRepository;
        this.dataValidator = dataValidator;
    }

    public SessionResponseDto createSession(SessionRequestDto sessionRequestDto) {
        dataValidator.validateSessionData(
                sessionRequestDto.getTitle(),
                sessionRequestDto.getDescription(),
                String.valueOf(sessionRequestDto.getStartDate()),
                String.valueOf(sessionRequestDto.getEndDate()),
                sessionRequestDto.getRoomName(),
                sessionRequestDto.getEventTitle(),
                String.valueOf(sessionRequestDto.getCapacity())
        );
        return sessionRepository.createSession(sessionRequestDto)
                .orElseThrow(() -> new ConflictException(String.format("Session with title '%s' already exists", sessionRequestDto.getTitle())));
    }

    public GetSessionListResponseDto getAllSessions(GetSessionRequestDto request, PaginationRequestDto pagination){
        dataValidator.validateSessionRequestData(request);
        return new GetSessionListResponseDto(
                sessionRepository.getAllSessions(request, pagination),
                sessionRepository.countFilteredSessions(request),
                pagination.getPage(),
                pagination.getSize()
        );
    }

    public SessionResponseDto updateSession(UUID id, SessionRequestDto session) {
        dataValidator.validateSessionData(
                session.getTitle(),
                session.getDescription(),
                String.valueOf(session.getStartDate()),
                String.valueOf(session.getEndDate()),
                session.getRoomName(),
                session.getEventTitle(),
                String.valueOf(session.getCapacity())
        );
        if (sessionRepository.findSessionByTitleExcludingId(session.getTitle(), id).isPresent()){
            throw new ConflictException(String.format("Session with title '%s' already exists.", session.getTitle()));
        }
        return sessionRepository.updateSessionById(id, session)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    public UUID deleteSessionById(UUID id) {
        return sessionRepository.deleteSessionById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }
}
