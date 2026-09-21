import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Inserts a new quiz (status DRAFT) and returns its generated id, or -1 on failure. */
    public int createQuiz(String title, int subjectId, int teacherId, int durationMinutes,
                           String deadline, String comment) {
        String sql = "INSERT INTO quizzes(title, subject_id, teacher_id, duration_minutes, " +
                "deadline, comment, status, created_at) VALUES (?, ?, ?, ?, ?, ?, 'DRAFT', ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setInt(2, subjectId);
            ps.setInt(3, teacherId);
            ps.setInt(4, durationMinutes);
            ps.setString(5, deadline);
            ps.setString(6, comment);
            ps.setString(7, LocalDateTime.now().format(TS));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateDetails(int quizId, String title, int subjectId, int durationMinutes,
                               String deadline, String comment) {
        String sql = "UPDATE quizzes SET title=?, subject_id=?, duration_minutes=?, deadline=?, comment=? WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, subjectId);
            ps.setInt(3, durationMinutes);
            ps.setString(4, deadline);
            ps.setString(5, comment);
            ps.setInt(6, quizId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void publish(int quizId) {
        setStatus(quizId, "PUBLISHED");
    }

    public void setStatus(int quizId, String status) {
        String sql = "UPDATE quizzes SET status=? WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, quizId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteQuiz(int quizId) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM questions WHERE quiz_id=?")) {
                ps.setInt(1, quizId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM results WHERE quiz_id=?")) {
                ps.setInt(1, quizId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM quizzes WHERE id=?")) {
                ps.setInt(1, quizId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        String sql = "SELECT q.*, s.name AS subject_name FROM quizzes q " +
                "JOIN subjects s ON q.subject_id = s.id WHERE teacher_id = ? ORDER BY created_at DESC";
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) quizzes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    /** Published quizzes for a subject, newest first. */
    public List<Quiz> getPublishedQuizzesBySubject(String subjectName) {
        String sql = "SELECT q.*, s.name AS subject_name FROM quizzes q " +
                "JOIN subjects s ON q.subject_id = s.id " +
                "WHERE q.status='PUBLISHED' AND s.name = ? ORDER BY q.deadline ASC";
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) quizzes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public Quiz getQuizById(int quizId) {
        String sql = "SELECT q.*, s.name AS subject_name FROM quizzes q " +
                "JOIN subjects s ON q.subject_id = s.id WHERE q.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Quiz mapRow(ResultSet rs) throws SQLException {
        return new Quiz(rs.getInt("id"), rs.getString("title"), rs.getInt("subject_id"),
                rs.getString("subject_name"), rs.getInt("teacher_id"), rs.getInt("duration_minutes"),
                rs.getString("deadline"), rs.getString("comment"), rs.getString("status"),
                rs.getString("created_at"));
    }
}
