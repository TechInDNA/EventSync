package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<SessionResponseDto> getAllSessions(PaginationRequestDto pagination) {
        return sessionRepository.getAllSessions(pagination.getOffset(), pagination.getLimit());
    }

    public int countSessions() {
        return sessionRepository.countSessions();
    }

    public UUID deleteSessionById(UUID id) {
        return sessionRepository.deleteSessionById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }
}
