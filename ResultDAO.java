import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void saveResult(int quizId, int studentId, int score, int total) {
        String sql = "INSERT INTO results(quiz_id, student_id, score, total, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, studentId);
            ps.setInt(3, score);
            ps.setInt(4, total);
            ps.setString(5, LocalDateTime.now().format(TS));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** True if this student has already submitted this quiz. */
    public boolean hasAttempted(int quizId, int studentId) {
        String sql = "SELECT 1 FROM results WHERE quiz_id = ? AND student_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** All results for one quiz, best score first (a simple leaderboard). */
    public List<Result> getResultsForQuiz(int quizId) {
        String sql = "SELECT r.*, q.title AS quiz_title, s.name AS subject_name, u.username " +
                "FROM results r " +
                "JOIN quizzes q ON r.quiz_id = q.id " +
                "JOIN subjects s ON q.subject_id = s.id " +
                "JOIN users u ON r.student_id = u.id " +
                "WHERE r.quiz_id = ? ORDER BY r.score DESC, r.submitted_at ASC";
        return runQuery(sql, quizId, -1);
    }

    /** All results for one student across every subject/quiz, most recent first. */
    public List<Result> getResultsForStudent(int studentId) {
        String sql = "SELECT r.*, q.title AS quiz_title, s.name AS subject_name, u.username " +
                "FROM results r " +
                "JOIN quizzes q ON r.quiz_id = q.id " +
                "JOIN subjects s ON q.subject_id = s.id " +
                "JOIN users u ON r.student_id = u.id " +
                "WHERE r.student_id = ? ORDER BY r.submitted_at DESC";
        return runQuery(sql, -1, studentId);
    }

    private List<Result> runQuery(String sql, int quizId, int studentId) {
        List<Result> results = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId != -1 ? quizId : studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Result(rs.getInt("id"), rs.getInt("quiz_id"), rs.getString("quiz_title"),
                            rs.getString("subject_name"), rs.getInt("student_id"), rs.getString("username"),
                            rs.getInt("score"), rs.getInt("total"), rs.getString("submitted_at")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
