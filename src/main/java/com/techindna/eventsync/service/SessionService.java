package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
