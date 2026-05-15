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
        UUID uuid = UUID.fromString(sessionId);

        sessionRepository.findSessionById(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("Session %s not found.", sessionId)));

        String sortField = (sort != null && sort.equals("upvote")) ? "upvote" : "createdAt";

        return new GetQuestionListResponseDto(
                questionRepository.getQuestionsBySessionId(uuid, sortField, pagination.getOffset(), pagination.getLimit()),
                questionRepository.countQuestionsBySessionId(uuid),
                pagination.getPage(),
                pagination.getSize()
        );
    }
}
