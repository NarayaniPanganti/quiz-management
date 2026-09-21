import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central place for the SQLite connection and schema setup.
 * The database file (quiz.db) is created automatically in the folder
 * the app is run from, the first time it starts.
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:quiz.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Creates all tables if they don't already exist. Call once at startup. */
    public static void initializeDatabase() {
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "email TEXT," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL)";

        String subjects = "CREATE TABLE IF NOT EXISTS subjects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL)";

        String quizzes = "CREATE TABLE IF NOT EXISTS quizzes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "subject_id INTEGER NOT NULL," +
                "teacher_id INTEGER NOT NULL," +
                "duration_minutes INTEGER NOT NULL," +
                "deadline TEXT," +
                "comment TEXT," +
                "status TEXT NOT NULL DEFAULT 'DRAFT'," +
                "created_at TEXT)";

        String questions = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_id INTEGER NOT NULL," +
                "question_text TEXT NOT NULL," +
                "option_a TEXT NOT NULL," +
                "option_b TEXT NOT NULL," +
                "option_c TEXT NOT NULL," +
                "option_d TEXT NOT NULL," +
                "correct_index INTEGER NOT NULL)";

        String results = "CREATE TABLE IF NOT EXISTS results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_id INTEGER NOT NULL," +
                "student_id INTEGER NOT NULL," +
                "score INTEGER NOT NULL," +
                "total INTEGER NOT NULL," +
                "submitted_at TEXT)";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute(users);
            st.execute(subjects);
            st.execute(quizzes);
            st.execute(questions);
            st.execute(results);
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Could not set up the database:\n" + e.getMessage(),
                    "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
