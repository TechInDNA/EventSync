package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ParticipantDto;
import com.techindna.eventsync.dto.QuestionResponseDto;
import com.techindna.eventsync.exception.InternalServerErrorException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class QuestionRepository {
    private final DataSource dataSource;

    public QuestionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean existsByIdAndSessionId(UUID questionId, UUID sessionId) {
        final String query = "SELECT 1 FROM eventsync_app.question WHERE id = ? AND session_id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, questionId);
            ps.setObject(2, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public void upvoteQuestion(UUID questionId, UUID userId) {

        final String query = """
            INSERT INTO eventsync_app.upvote (question_id, user_id)
            VALUES (?, ?)
            ON CONFLICT (question_id, user_id) DO NOTHING
            """;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, questionId);
            ps.setObject(2, userId);
            ps.executeUpdate(); // Utilisation de executeUpdate si on retire le RETURNING id qui n'est pas strictement vital ici
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<QuestionResponseDto> getQuestionsBySessionId(UUID sessionId, String sort, int offset, int limit) {
        final String orderClause = "creationDate".equals(sort)
                ? "ORDER BY q.created_at DESC"
                : "ORDER BY upvote_count DESC, q.created_at DESC";

        final String query = """
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

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {

            ps.setObject(1, sessionId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            List<QuestionResponseDto> questions = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionResponseDto q = new QuestionResponseDto();
                    // Correction ici : getObject au lieu de getString + UUID.fromString
                    q.setId(rs.getObject("id", UUID.class));
                    q.setTitle(rs.getString("title"));
                    q.setContent(rs.getString("content"));

                    // Correction ici : Utilisation de OffsetDateTime plus robuste avec JDBC
                    if (rs.getTimestamp("created_at") != null) {
                        q.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class).toInstant());
                    }

                    q.setAnonymous(rs.getBoolean("anonymous"));
                    q.setUpvotes(rs.getInt("upvote_count"));

                    if (q.isAnonymous()) {
                        q.setParticipant(null);
                    } else {
                        ParticipantDto participant = new ParticipantDto();
                        participant.setId(rs.getObject("user_id", UUID.class));
                        participant.setFirstName(rs.getString("first_name"));
                        participant.setLastName(rs.getString("last_name"));
                        participant.setEmail(rs.getString("email"));
                        q.setParticipant(participant);
                    }

                    questions.add(q);
                }
            }
            return questions;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public int countQuestionsBySessionId(UUID sessionId) {
        String query = "SELECT COUNT(id) as total FROM eventsync_app.question WHERE session_id = ?";

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