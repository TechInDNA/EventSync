package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.EventRepository;
import com.techindna.eventsync.repository.RoomRepository;
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
                String.valueOf(sessionRequestDto.getRoomId()),
                String.valueOf(sessionRequestDto.getEventId()),
                sessionRequestDto.getCapacity()
        );
        return sessionRepository.createSession(sessionRequestDto);
    }

    public UUID deleteSessionById(UUID id) {
        return sessionRepository.deleteSessionById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }
}
