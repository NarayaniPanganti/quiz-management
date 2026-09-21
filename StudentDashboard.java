import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class StudentDashboard extends JFrame {

    private final User student;
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    private JComboBox<String> subjectBox;
    private final DefaultListModel<Quiz> listModel = new DefaultListModel<>();
    private final JList<Quiz> quizList = new JList<>(listModel);

    public StudentDashboard(User student) {
        this.student = student;

        setTitle("Student Dashboard - " + student.getUsername());
        setSize(520, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Subject:"));
        List<String> subjects = subjectDAO.getAllSubjectNames();
        subjectBox = new JComboBox<>(subjects.toArray(new String[0]));
        topPanel.add(subjectBox);
        JButton historyBtn = new JButton("My History");
        JButton logoutBtn = new JButton("Logout");
        topPanel.add(historyBtn);
        topPanel.add(logoutBtn);

        quizList.setCellRenderer((list, quiz, index, isSelected, hasFocus) -> {
            boolean attempted = resultDAO.hasAttempted(quiz.getId(), student.getId());
            boolean expired = isPastDeadline(quiz.getDeadline());
            String status = attempted ? "COMPLETED" : expired ? "DEADLINE PASSED" : "AVAILABLE";
            String text = String.format("%s  \u2022 %d min  \u2022 due %s  \u2022 %s",
                    quiz.getTitle(), quiz.getDurationMinutes(), quiz.getDeadline(), status);
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            Color base = attempted ? new Color(210, 210, 210)
                    : expired ? new Color(255, 200, 200)
                    : new Color(210, 235, 255);
            label.setBackground(isSelected ? base.darker() : base);
            return label;
        });
        JScrollPane scroll = new JScrollPane(quizList);

        JButton attemptBtn = new JButton("Attempt Selected Quiz");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(attemptBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshList();

        subjectBox.addActionListener(e -> refreshList());

        attemptBtn.addActionListener(e -> {
            Quiz selected = quizList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a quiz first.");
                return;
            }
            if (resultDAO.hasAttempted(selected.getId(), student.getId())) {
                JOptionPane.showMessageDialog(this, "You've already submitted this quiz.");
                return;
            }
            if (isPastDeadline(selected.getDeadline())) {
                JOptionPane.showMessageDialog(this, "The deadline for this quiz has passed.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Start \"" + selected.getTitle() + "\"? You'll have " + selected.getDurationMinutes()
                            + " minutes once you begin.",
                    "Start Quiz", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new QuizAttemptScreen(student, selected);
            }
        });

        historyBtn.addActionListener(e -> {
            dispose();
            new HistoryScreen(student);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        setVisible(true);
    }

    private boolean isPastDeadline(String deadline) {
        try {
            LocalDateTime dl = LocalDateTime.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return LocalDateTime.now().isAfter(dl);
        } catch (Exception e) {
            return false;
        }
    }

    private void refreshList() {
        listModel.clear();
        String subject = (String) subjectBox.getSelectedItem();
        if (subject == null) return;
        List<Quiz> quizzes = quizDAO.getPublishedQuizzesBySubject(subject);
        for (Quiz q : quizzes) listModel.addElement(q);
    }
}
