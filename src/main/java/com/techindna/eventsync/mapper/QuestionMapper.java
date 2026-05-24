package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.QuestionResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class QuestionMapper {

    public static QuestionResponseDto mapResultSetToQuestionResponseDto(ResultSet rs) throws SQLException {
        QuestionResponseDto q = new QuestionResponseDto();
        q.setId(UUID.fromString(rs.getString("id")));
        q.setTitle(rs.getString("title"));
        q.setContent(rs.getString("content"));
        q.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        q.setAnonymous(rs.getBoolean("anonymous"));
        q.setUpvotes(rs.getInt("upvote_count"));
        return q;
    }
}
