package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.entity.Question;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final QuestionService questionService;
    private final DataValidator dataValidator;

    public SessionService(SessionRepository sessionRepository, QuestionService questionService, DataValidator dataValidator) {
        this.sessionRepository = sessionRepository;
        this.questionService = questionService;
        this.dataValidator = dataValidator;
    }




    public List<Session> getAllSessions(PaginationRequestDto pagination,
                                        String room, String speaker, Boolean live, String event) {
        List<Session> sessions = sessionRepository.getAllSessions(
                pagination.getOffset(), pagination.getLimit(), room, speaker, live, event);

        if (!sessions.isEmpty()) {
            List<UUID> sessionIds = sessions.stream().map(Session::getId).toList();
            Map<UUID, List<Question>> questionsBySession = questionService.getQuestionsBySessionIds(sessionIds);
            for (Session s : sessions) {
                s.setQuestions(questionsBySession.getOrDefault(s.getId(), List.of()));
            }
        }

        return sessions;
    }

    public int countSessions(String room, String speaker, Boolean live, String event) {
        return sessionRepository.countSessions(room, speaker, live, event);
    }

    public Session getSessionById(UUID id) {
        Session session = sessionRepository.findSessionById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", id)));

        List<Question> questions = questionService.getQuestionsBySessionIds(List.of(id))
                .getOrDefault(id, List.of());
        session.setQuestions(questions);

        return session;
    }

    public Session updateSession(UUID id, String title, String description, Instant startDate,
                                 Instant endDate, UUID roomId, int capacity, UUID eventId) {

        Optional<Session> existing = sessionRepository.findSessionByTitle(title);
        if (existing.isPresent()) {
            Session s = existing.get();
            if (!s.getId().equals(id)) {
                throw new ConflictException(String.format(
                        "A session with title '%s' already exists (ID: %s)", s.getTitle(), s.getId()));
            }
        }
        sessionRepository.updateSession(id, title, description, startDate, endDate, roomId, capacity, eventId);

        return getSessionById(id);
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
