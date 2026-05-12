package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Question;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getQuestionsBySessionId(UUID sessionId, String sort, PaginationRequestDto pagination) {
        List<Question> questions = questionRepository.getQuestionsBySessionId(sessionId, sort,
                pagination.getOffset(), pagination.getLimit());

        List<java.util.Map<String, Object>> allVotes = questionRepository.findAllVotesBySessionId(sessionId);

        for (Question q : questions) {
            int count = 0;
            for (java.util.Map<String, Object> vote : allVotes) {
                UUID voteQuestionId = (UUID) vote.get("question_id");
                if (q.getId().equals(voteQuestionId)) {
                    count++;
                }
            }
            q.setUpvotes(count);
        }

        return questions;
    }

    public Map<UUID, List<Question>> getQuestionsBySessionIds(List<UUID> sessionIds) {
        Map<UUID, List<Question>> questionsBySession = questionRepository.getQuestionsBySessionIds(sessionIds);

        for (Map.Entry<UUID, List<Question>> entry : questionsBySession.entrySet()) {
            List<Map<String, Object>> votes = questionRepository.findAllVotesBySessionId(entry.getKey());
            for (Question q : entry.getValue()) {
                int count = 0;
                for (Map<String, Object> vote : votes) {
                    UUID voteQuestionId = (UUID) vote.get("question_id");
                    if (q.getId().equals(voteQuestionId)) {
                        count++;
                    }
                }
                q.setUpvotes(count);
            }
        }

        return questionsBySession;
    }

    public int countQuestionsBySessionId(UUID sessionId) {
        return questionRepository.countQuestionsBySessionId(sessionId);
    }

    public Question createQuestion(String title, String content, UUID sessionId, UUID userId, boolean isAnonymous) {
        if (!questionRepository.isSessionLive(sessionId)) {
            throw new BadRequestException("Session is not live - questions are disabled.");
        }
        Question question = questionRepository.saveQuestion(title, content, sessionId, userId, isAnonymous);
        question.setUpvotes(0);
        return question;
    }

    public int upvoteQuestion(UUID questionId) {
        Question q = questionRepository.findQuestionById(questionId)
                .orElseThrow(() -> new NotFoundException(String.format("Question %s not found.", questionId)));

        questionRepository.upvoteQuestion(q.getId(), null);

        int count = 0;
        List<java.util.Map<String, Object>> allVotes = questionRepository.findAllVotesByQuestionId(q.getId());
        for (java.util.Map<String, Object> vote : allVotes) {
            UUID voteQuestionId = (UUID) vote.get("question_id");
            if (q.getId().equals(voteQuestionId)) {
                count++;
            }
        }

        return count;
    }
}
