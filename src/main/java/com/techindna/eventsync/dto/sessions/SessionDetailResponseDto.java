package com.techindna.eventsync.dto.sessions;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.dto.QuestionResponseDto;
import java.util.List;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "room", "capacity", "event", "speakers", "questions", "isLive"})
public class SessionDetailResponseDto extends SessionResponseDto {
    private List<QuestionResponseDto> questions;

    public List<QuestionResponseDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponseDto> questions) {
        this.questions = questions;
    }
}
