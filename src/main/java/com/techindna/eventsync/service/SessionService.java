package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
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

    @Transactional
    public LinkSpeakerResponseDto linkSpeakerToSession(String sessionId, String speakerId, LinkSpeakerRequestDto request) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateUUID(speakerId);

        UUID sessionUuid = UUID.fromString(sessionId);
        UUID speakerUuid = UUID.fromString(speakerId);

        sessionRepository.findSessionById(sessionUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        speakerRepository.findSpeakerById(speakerUuid)
                .orElseThrow(() -> new NotFoundException(String.format("Speaker %s not found.", speakerId)));

        checkSpeakerLinkAuthorization(speakerId);

        return sessionRepository.linkSpeakerToSession(sessionUuid, speakerUuid,
                request.getStartTime(), request.getEndTime());
    }


    private void checkSpeakerLinkAuthorization(String speakerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }
        String currentUserId = authentication.getPrincipal().toString();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        boolean isSpeaker = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SPEAKER"));

        if (isSpeaker && speakerId.equals(currentUserId)) {
            return;
        }

        throw new UnauthorizedException(
                "You are not authorized to link this speaker to a session");
    }
}
