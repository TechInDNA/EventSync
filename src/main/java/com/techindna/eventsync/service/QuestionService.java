package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.GetQuestionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.repository.QuestionRepository;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;
    private final DataValidator dataValidator;

    public QuestionService(QuestionRepository questionRepository, SessionRepository sessionRepository, DataValidator dataValidator) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
        this.dataValidator = dataValidator;
    }

    public GetQuestionListResponseDto getQuestionsBySessionId(String sessionId, String sort, PaginationRequestDto pagination) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateSortByQuestion(sort);

        UUID sid = UUID.fromString(sessionId);

        sessionRepository.findSessionById(sid)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        return new GetQuestionListResponseDto(
                questionRepository.getQuestionsBySessionId(sid, sort, pagination.getOffset(), pagination.getLimit()),
                questionRepository.countQuestionsBySessionId(sid),
                pagination.getPage(),
                pagination.getSize()
        );
    }

    public void upvoteQuestion(String sessionId, String questionId) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateUUID(questionId);

        UUID sid = UUID.fromString(sessionId);
        UUID qid = UUID.fromString(questionId);

        if (!questionRepository.existsByIdAndSessionId(qid, sid)) {
            throw new NotFoundException(String.format("Question %s not found in session %s.", questionId, sessionId));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("User is not authenticated");
        }

        UUID userId = UUID.fromString(auth.getName());

        questionRepository.upvoteQuestion(qid, userId);
    }
}