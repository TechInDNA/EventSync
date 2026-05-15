package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.GetQuestionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.repository.QuestionRepository;
import com.techindna.eventsync.repository.SessionRepository;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.validator.DataValidator;
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

        sessionRepository.findSessionById(UUID.fromString(sessionId))
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        return new GetQuestionListResponseDto(
                questionRepository.getQuestionsBySessionId(UUID.fromString(sessionId), sort, pagination.getOffset(), pagination.getLimit()),
                questionRepository.countQuestionsBySessionId(UUID.fromString(sessionId)),
                pagination.getPage(),
                pagination.getSize()
        );
    }
}
