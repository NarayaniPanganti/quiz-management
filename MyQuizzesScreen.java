import java.awt.*;
import java.util.List;
import javax.swing.*;

public class MyQuizzesScreen extends JFrame {

    private final User teacher;
    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final DefaultListModel<Quiz> listModel = new DefaultListModel<>();
    private final JList<Quiz> quizList = new JList<>(listModel);

    public MyQuizzesScreen(User teacher) {
        this.teacher = teacher;

        setTitle("My Quizzes");
        setSize(500, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("  My Quizzes  (orange = draft, green = published)");
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));

        quizList.setCellRenderer((list, quiz, index, isSelected, hasFocus) -> {
            int qCount = questionDAO.countQuestions(quiz.getId());
            String text = String.format("%s  [%s]  \u2022 %d question(s)  \u2022 due %s",
                    quiz.getTitle(), quiz.getSubjectName(), qCount, quiz.getDeadline());
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            Color base = quiz.isDraft() ? new Color(255, 224, 178) : new Color(200, 240, 200);
            label.setBackground(isSelected ? base.darker() : base);
            return label;
        });

        JScrollPane scroll = new JScrollPane(quizList);

        JButton openBtn = new JButton("Open");
        JButton deleteBtn = new JButton("Delete");
        JButton backBtn = new JButton("Back");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(openBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);

        add(heading, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshList();

        // Draft -> continue editing; Published -> view results/leaderboard
        openBtn.addActionListener(e -> openSelected());
        quizList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openSelected();
            }
        });

        deleteBtn.addActionListener(e -> {
            Quiz selected = quizList.getSelectedValue();
            if (selected == null) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + selected.getTitle() + "\" and all its questions/results?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                quizDAO.deleteQuiz(selected.getId());
                refreshList();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new TeacherDashboard(teacher);
        });

        setVisible(true);
    }

    private void openSelected() {
        Quiz selected = quizList.getSelectedValue();
        if (selected == null) return;
        dispose();
        if (selected.isDraft()) {
            new QuestionEditorScreen(teacher, selected.getId());
        } else {
            new QuizResultsScreen(teacher, selected);
        }
    }

    private void refreshList() {
        listModel.clear();
        List<Quiz> quizzes = quizDAO.getQuizzesByTeacher(teacher.getId());
        for (Quiz q : quizzes) listModel.addElement(q);
    }
}
