import java.awt.*;
import java.util.List;
import javax.swing.*;

public class QuizAttemptScreen extends JFrame {

    private static final char NOT_VISITED = 'U';
    private static final char ANSWERED = 'A';
    private static final char LEFT_FOR_LATER = 'L';
    private static final char SKIPPED = 'S';

    private final User student;
    private final Quiz quiz;
    private final List<Question> questions;
    private final int[] selectedAnswers;
    private final char[] status;

    private int currentIndex = 0;
    private int remainingSeconds;
    private javax.swing.Timer countdownTimer;
    private boolean submitted = false;

    private final JLabel timerLabel = new JLabel();
    private final JLabel questionLabel = new JLabel();
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private final JPanel navGrid = new JPanel();
    private final JButton[] navButtons;

    public QuizAttemptScreen(User student, Quiz quiz) {
        this.student = student;
        this.quiz = quiz;
        this.questions = new QuestionDAO().getQuestionsForQuiz(quiz.getId());
        this.selectedAnswers = new int[questions.size()];
        this.status = new char[questions.size()];
        this.navButtons = new JButton[questions.size()];
        java.util.Arrays.fill(selectedAnswers, -1);
        java.util.Arrays.fill(status, NOT_VISITED);
        this.remainingSeconds = quiz.getDurationMinutes() * 60;

        setTitle("Attempting: " + quiz.getTitle());
        setSize(700, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // force using Submit
        setLayout(new BorderLayout(10, 10));

        // ---- top: title + timer ----
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("  " + quiz.getTitle() + " [" + quiz.getSubjectName() + "]");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);

        // ---- center: question + options ----
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            optionGroup.add(optionButtons[i]);
            centerPanel.add(optionButtons[i]);
            centerPanel.add(Box.createVerticalStrut(8));
            final int idx = i;
            optionButtons[i].addActionListener(e -> {
                selectedAnswers[currentIndex] = idx;
                status[currentIndex] = ANSWERED;
                refreshNavGrid();
            });
        }

        // ---- navigation grid (color-coded question matrix) ----
        navGrid.setLayout(new GridLayout(0, 8, 4, 4));
        navGrid.setBorder(BorderFactory.createTitledBorder("Questions (green=answered, orange=left for later, red=skipped)"));
        for (int i = 0; i < questions.size(); i++) {
            final int idx = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.addActionListener(e -> goToQuestion(idx));
            navButtons[i] = btn;
            navGrid.add(btn);
        }
        JScrollPane navScroll = new JScrollPane(navGrid);
        navScroll.setPreferredSize(new Dimension(180, 400));

        // ---- bottom: prev / skip / next / submit ----
        JButton prevBtn = new JButton("\u2190 Previous");
        JButton skipBtn = new JButton("Skip");
        JButton nextBtn = new JButton("Next \u2192");
        JButton submitBtn = new JButton("Submit Quiz");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(prevBtn);
        bottomPanel.add(skipBtn);
        bottomPanel.add(nextBtn);
        bottomPanel.add(submitBtn);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(navScroll, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        prevBtn.addActionListener(e -> goToQuestion(Math.max(0, currentIndex - 1)));
        nextBtn.addActionListener(e -> goToQuestion(Math.min(questions.size() - 1, currentIndex + 1)));
        skipBtn.addActionListener(e -> {
            status[currentIndex] = SKIPPED;
            selectedAnswers[currentIndex] = -1;
            optionGroup.clearSelection();
            refreshNavGrid();
            goToQuestion(Math.min(questions.size() - 1, currentIndex + 1));
        });
        submitBtn.addActionListener(e -> confirmAndSubmit(false));

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This quiz has no questions yet.");
            dispose();
            new StudentDashboard(student);
            return;
        }

        loadQuestion(0);
        status[0] = NOT_VISITED; // stays not-visited-colored until answered/left/skipped
        startTimer();
        setVisible(true);
    }

    private void goToQuestion(int newIndex) {
        if (status[currentIndex] == NOT_VISITED) {
            status[currentIndex] = LEFT_FOR_LATER;
        }
        loadQuestion(newIndex);
        refreshNavGrid();
    }

    private void loadQuestion(int index) {
        currentIndex = index;
        Question q = questions.get(index);
        questionLabel.setText("<html><b>Q" + (index + 1) + ".</b> " + escape(q.getQuestionText()) + "</html>");
        String[] choices = q.getChoices();
        optionGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(choices[i]);
        }
        if (selectedAnswers[index] != -1) {
            optionButtons[selectedAnswers[index]].setSelected(true);
        }
        refreshNavGrid();
    }

    private void refreshNavGrid() {
        for (int i = 0; i < navButtons.length; i++) {
            JButton btn = navButtons[i];
            Color color;
            switch (status[i]) {
                case ANSWERED: color = new Color(150, 220, 150); break;
                case LEFT_FOR_LATER: color = new Color(255, 200, 120); break;
                case SKIPPED: color = new Color(240, 150, 150); break;
                default: color = Color.LIGHT_GRAY;
            }
            btn.setBackground(color);
            btn.setOpaque(true);
            btn.setBorder(i == currentIndex
                    ? BorderFactory.createLineBorder(Color.BLUE, 2)
                    : BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    private void startTimer() {
        updateTimerLabel();
        countdownTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Your quiz will be submitted now.");
                doSubmit();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerLabel() {
        int m = Math.max(0, remainingSeconds) / 60;
        int s = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("Time left: %02d:%02d", m, s));
    }

    private void confirmAndSubmit(boolean auto) {
        int unanswered = 0;
        for (int a : selectedAnswers) if (a == -1) unanswered++;
        String msg = unanswered > 0
                ? "You have " + unanswered + " unanswered question(s). Submit anyway?"
                : "Submit your answers now?";
        int confirm = JOptionPane.showConfirmDialog(this, msg, "Submit Quiz", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            doSubmit();
        }
    }

    private void doSubmit() {
        if (submitted) return;
        submitted = true;
        if (countdownTimer != null) countdownTimer.stop();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (selectedAnswers[i] == questions.get(i).getCorrectIndex()) score++;
        }
        int total = questions.size();
        new ResultDAO().saveResult(quiz.getId(), student.getId(), score, total);

        dispose();
        new ResultScreen(student, quiz, score, total);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
