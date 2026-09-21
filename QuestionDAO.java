import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public int addQuestion(int quizId, String text, String[] choices, int correctIndex) {
        String sql = "INSERT INTO questions(quiz_id, question_text, option_a, option_b, option_c, option_d, correct_index) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, quizId);
            ps.setString(2, text);
            ps.setString(3, choices[0]);
            ps.setString(4, choices[1]);
            ps.setString(5, choices[2]);
            ps.setString(6, choices[3]);
            ps.setInt(7, correctIndex);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateQuestion(int questionId, String text, String[] choices, int correctIndex) {
        String sql = "UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_index=? WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, text);
            ps.setString(2, choices[0]);
            ps.setString(3, choices[1]);
            ps.setString(4, choices[2]);
            ps.setString(5, choices[3]);
            ps.setInt(6, correctIndex);
            ps.setInt(7, questionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getQuestionsForQuiz(int quizId) {
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY id ASC";
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] choices = {
                            rs.getString("option_a"), rs.getString("option_b"),
                            rs.getString("option_c"), rs.getString("option_d")
                    };
                    questions.add(new Question(rs.getInt("id"), rs.getInt("quiz_id"),
                            rs.getString("question_text"), choices, rs.getInt("correct_index")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public int countQuestions(int quizId) {
        String sql = "SELECT COUNT(*) AS c FROM questions WHERE quiz_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
