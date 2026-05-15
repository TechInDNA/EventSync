package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.QuestionResponseDto;
import com.techindna.eventsync.exception.InternalServerErrorException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class QuestionRepository {
    private final DataSource dataSource;

    public QuestionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<QuestionResponseDto> getQuestionsBySessionId(UUID sessionId, String sort, int offset, int limit) {
        String orderClause = "createdAt".equals(sort)
                ? "ORDER BY q.created_at DESC"
                : "ORDER BY upvote_count DESC, q.created_at DESC";

        String query = """
            SELECT q.id, q.title, q.content, q.created_at, q.anonymous,
                   u.id as user_id, u.first_name, u.last_name, u.email,
                   COUNT(up.id) as upvote_count
            FROM eventsync_app.question q
            JOIN eventsync_app.users u ON q.user_id = u.id
            LEFT JOIN eventsync_app.upvote up ON up.question_id = q.id
            WHERE q.session_id = ?
            GROUP BY q.id, q.title, q.content, q.created_at, q.anonymous,
                     u.id, u.first_name, u.last_name, u.email
            """ + orderClause + " LIMIT ? OFFSET ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setObject(1, sessionId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            List<QuestionResponseDto> questions = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionResponseDto q = new QuestionResponseDto();
                    q.setId(UUID.fromString(rs.getString("id")));
                    q.setTitle(rs.getString("title"));
                    q.setContent(rs.getString("content"));
                    q.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    q.setAnonymous(rs.getBoolean("anonymous"));
                    q.setUpvotes(rs.getInt("upvote_count"));

                    QuestionResponseDto.ParticipantDto participant = new QuestionResponseDto.ParticipantDto();
                    participant.setId(UUID.fromString(rs.getString("user_id")));
                    participant.setFirstName(rs.getString("first_name"));
                    participant.setLastName(rs.getString("last_name"));
                    participant.setEmail(rs.getString("email"));
                    q.setParticipant(participant);

                    questions.add(q);
                }
            }
            return questions;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public int countQuestionsBySessionId(UUID sessionId) {
        String query = "SELECT COUNT(*) as total FROM eventsync_app.question WHERE session_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setObject(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
