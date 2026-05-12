package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.ParticipantRef;
import com.techindna.eventsync.entity.Question;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class QuestionRepository {

    private final DataSource dataSource;

    public QuestionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(UUID.fromString(rs.getString("id")));
        q.setTitle(rs.getString("title"));
        q.setContent(rs.getString("content"));
        q.setUpvotes(0);
        q.setIsAnonymous(rs.getBoolean("anonymous"));
        q.setCreatedAt(rs.getTimestamp("created_at").toInstant());

        boolean isAnonymous = rs.getBoolean("anonymous");
        UUID userId = rs.getObject("user_id", java.util.UUID.class);
        if (userId != null && !isAnonymous) {
            ParticipantRef p = new ParticipantRef();
            p.setId(userId);
            p.setFirstName(rs.getString("first_name"));
            p.setLastName(rs.getString("last_name"));
            p.setEmail(rs.getString("email"));
            q.setParticipant(p);
        }

        return q;
    }

    public List<Question> getQuestionsBySessionId(UUID sessionId, String sort, int offset, int limit) {
        String orderClause = "upvotes".equals(sort)
                ? "(select count(*) from eventsync_app.upvotes v where v.question_id = q.id) desc"
                : "q.created_at asc";

        final String query =
        """
        select
            q.id, q.title, q.content, q.anonymous, q.created_at,
            q.user_id, u.first_name, u.last_name, u.email
        from
            eventsync_app.questions q
            left join eventsync_app.users u on q.user_id = u.id
        where
            q.session_id = ?

        order by """ + orderClause + """
        limit ? offset ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Question> questions = new ArrayList<>();
                while (rs.next()) {
                    questions.add(mapRow(rs));
                }
                return questions;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, List<Question>> getQuestionsBySessionIds(List<UUID> sessionIds) {
        if (sessionIds.isEmpty()) return Map.of();

        StringBuilder query = new StringBuilder(
        """
        select
            q.id, q.title, q.content, q.anonymous, q.created_at,
            q.user_id, u.first_name, u.last_name, u.email,
            q.session_id
        from
            eventsync_app.questions q
            left join eventsync_app.users u on q.user_id = u.id
        where q.session_id in ("""
        );
        for (int i = 0; i < sessionIds.size(); i++) {
            if (i > 0) query.append(",");
            query.append("?");
        }
        query.append(")");

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query.toString())
        ) {
            for (int i = 0; i < sessionIds.size(); i++) {
                ps.setObject(i + 1, sessionIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                Map<UUID, List<Question>> result = new java.util.LinkedHashMap<>();
                while (rs.next()) {
                    UUID sessionId = UUID.fromString(rs.getString("session_id"));
                    Question q = mapRow(rs);
                    result.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(q);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countQuestionsBySessionId(UUID sessionId) {
        final String query =
        """
        select count(id) as total
        from eventsync_app.questions
        where session_id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Question saveQuestion(String title, String content, UUID sessionId, UUID userId, boolean isAnonymous) {
        final String query =
        """
        insert into
            eventsync_app.questions(title, content, session_id, user_id, anonymous)
        values(?, ?, ?, ?, ?)
        returning id, created_at
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setObject(3, sessionId);
            ps.setObject(4, userId);
            ps.setBoolean(5, isAnonymous);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Failed to create question");
                }
                Question q = new Question();
                q.setId(UUID.fromString(rs.getString("id")));
                q.setTitle(title);
                q.setContent(content);
                q.setIsAnonymous(isAnonymous);
                q.setCreatedAt(rs.getTimestamp("created_at").toInstant());

                if (userId != null && !isAnonymous) {
                    ParticipantRef p = new ParticipantRef();
                    p.setId(userId);
                    q.setParticipant(p);
                }
                return q;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSessionLive(UUID sessionId) {
        final String query =
        """
        select 1 from eventsync_app.sessions
        where id = ? and start_date <= now() and end_date > now()
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int upvoteQuestion(UUID questionId, UUID userId) {
        final String query =
        """
        insert into eventsync_app.upvotes(user_id, question_id)
        values(?, ?)
        on conflict (user_id, question_id) do nothing
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, userId);
            ps.setObject(2, questionId);
            ps.executeUpdate();
            return countVotesByQuestionId(questionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countVotesByQuestionId(UUID questionId) {
        final String query =
        """
        select count(*) as total
        from eventsync_app.upvotes
        where question_id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int upvoteQuestion(UUID questionId) {
        return upvoteQuestion(questionId, null);
    }

    public List<java.util.Map<String, Object>> findAllVotesBySessionId(UUID sessionId) {
        final String query =
        """
        select v.id, v.user_id, v.question_id, v.created_at
        from eventsync_app.upvotes v
        join eventsync_app.questions q on v.question_id = q.id
        where q.session_id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<java.util.Map<String, Object>> votes = new ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> vote = new java.util.HashMap<>();
                    vote.put("id", UUID.fromString(rs.getString("id")));
                    if (rs.getObject("user_id") != null) {
                        vote.put("user_id", UUID.fromString(rs.getString("user_id")));
                    }
                    vote.put("question_id", UUID.fromString(rs.getString("question_id")));
                    vote.put("created_at", rs.getTimestamp("created_at").toInstant());
                    votes.add(vote);
                }
                return votes;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<java.util.Map<String, Object>> findAllVotesByQuestionId(UUID questionId) {
        final String query =
        """
        select v.id, v.user_id, v.question_id, v.created_at
        from eventsync_app.upvotes v
        where v.question_id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<java.util.Map<String, Object>> votes = new ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, Object> vote = new java.util.HashMap<>();
                    vote.put("id", UUID.fromString(rs.getString("id")));
                    if (rs.getObject("user_id") != null) {
                        vote.put("user_id", UUID.fromString(rs.getString("user_id")));
                    }
                    vote.put("question_id", UUID.fromString(rs.getString("question_id")));
                    vote.put("created_at", rs.getTimestamp("created_at").toInstant());
                    votes.add(vote);
                }
                return votes;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Question> findQuestionById(UUID questionId) {
        final String query =
        """
        select
            q.id, q.title, q.content, q.anonymous, q.created_at,
            q.user_id, u.first_name, u.last_name, u.email
        from
            eventsync_app.questions q
            left join eventsync_app.users u on q.user_id = u.id
        where q.id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
