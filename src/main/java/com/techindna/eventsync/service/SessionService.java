package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetTime;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SpeakerRepository speakerRepository;
    private final DataValidator dataValidator;

    public SessionService(SessionRepository sessionRepository, SpeakerRepository speakerRepository, DataValidator dataValidator) {
        this.sessionRepository = sessionRepository;
        this.speakerRepository = speakerRepository;
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

    public void deleteSessionById(String id) {
        dataValidator.validateUUID(id);
        sessionRepository.deleteSessionById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    public void linkSpeakerToSession(String sessionId, String speakerId, LinkSpeakerRequestDto request) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateUUID(speakerId);
        UUID sId = UUID.fromString(sessionId);
        UUID spId = UUID.fromString(speakerId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        String currentUserId = (String) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        boolean isSpeakerSelf = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_SPEAKER"::equals)
                && currentUserId.equals(speakerId);
        if (!isAdmin && !isSpeakerSelf) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }

        sessionRepository.findSessionById(sId)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        speakerRepository.findSpeakerById(spId)
                .orElseThrow(() -> new NotFoundException(String.format("Speaker %s not found.", speakerId)));

        OffsetTime startTime = OffsetTime.parse(request.getStartTime());
        OffsetTime endTime = OffsetTime.parse(request.getEndTime());

        sessionRepository.linkSpeakerToSession(sId, spId, startTime, endTime);
    }
}
