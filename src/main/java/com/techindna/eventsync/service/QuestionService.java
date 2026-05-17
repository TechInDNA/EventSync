package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.GetQuestionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.exception.UnauthorizedException;
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

    public int upvoteQuestion(String sessionId, String questionId) {
        dataValidator.validateUUID(sessionId);
        dataValidator.validateUUID(questionId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized.");
        }

        UUID userId = UUID.fromString(auth.getName());

        questionRepository.findQuestionByIdAndSessionId(UUID.fromString(questionId), UUID.fromString(sessionId))
                .orElseThrow(() -> new NotFoundException(String.format("Question %s not found.", questionId)));

        return questionRepository.upvoteQuestion(UUID.fromString(questionId), userId);
    }
}