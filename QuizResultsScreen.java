import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QuizResultsScreen extends JFrame {

    public QuizResultsScreen(User teacher, Quiz quiz) {
        setTitle("Results - " + quiz.getTitle());
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("  " + quiz.getTitle() + "  [" + quiz.getSubjectName() + "]  \u2014 Leaderboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));

        ResultDAO resultDAO = new ResultDAO();
        List<Result> results = resultDAO.getResultsForQuiz(quiz.getId());

        String[] columns = {"Rank", "Student", "Score", "Percentage", "Submitted"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        int rank = 1;
        for (Result r : results) {
            model.addRow(new Object[]{
                    rank++, r.getStudentUsername(), r.getScore() + " / " + r.getTotal(),
                    String.format("%.1f%%", r.getPercentage()), r.getSubmittedAt()
            });
        }
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JLabel emptyLabel = new JLabel("No one has attempted this quiz yet.", SwingConstants.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            new MyQuizzesScreen(teacher);
        });

        add(heading, BorderLayout.NORTH);
        add(results.isEmpty() ? emptyLabel : scroll, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);

        setVisible(true);
    }
}
