package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> getAllSessions(PaginationRequestDto pagination) {
        return sessionRepository.getAllSessions(pagination.getOffset(), pagination.getLimit());
    }

    public int countSessions() {
        return sessionRepository.countSessions();
    }

    public Session createSession(String title, String description, Instant startDate, Instant endDate, UUID roomId, int capacity, UUID eventId) {
        Session newSession = sessionRepository.saveSession(title, description, startDate, endDate, roomId, capacity, eventId);
        if (newSession.getId() == null) {
            throw new ConflictException(String.format("Session with title '%s' already exists", title));
        }
        return newSession;
    }

    public Session getSessionById(UUID id) {
        return sessionRepository.findSessionById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));
    }

    public Session updateSession(UUID id, String title, String description, Instant startDate, Instant endDate, UUID roomId, int capacity, UUID eventId) {
        Optional<Session> existing = sessionRepository.findSessionByTitle(title);
        if (existing.isPresent()) {
            Session s = existing.get();
            if (!s.getId().equals(id)) {
                throw new ConflictException(String.format(
                        "A session with title '%s' already exists (ID: %s)", s.getTitle(), s.getId()));
            }
        }
        return sessionRepository.updateSession(id, title, description, startDate, endDate, roomId, capacity, eventId);
    }

    public UUID deleteSessionById(UUID id) {
        return sessionRepository.deleteSessionById(id);
    }

    public void removeSpeakerFromSession(UUID sessionId, UUID speakerId) {
        sessionRepository.removeSpeakerFromSession(sessionId, speakerId);
    }
}
