import java.awt.*;
import javax.swing.*;

public class ResultScreen extends JFrame {

    private static final double PASS_THRESHOLD = 40.0; // percent

    public ResultScreen(User student, Quiz quiz, int score, int total) {
        setTitle("Your Result");
        setSize(380, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        double percentage = total == 0 ? 0 : (score * 100.0) / total;
        boolean passed = percentage >= PASS_THRESHOLD;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel(quiz.getTitle());
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        CircleProgressPanel circle = new CircleProgressPanel(percentage, PASS_THRESHOLD);
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Score: " + score + " / " + total, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(passed ? "PASSED" : "NOT PASSED (pass mark: " + (int) PASS_THRESHOLD + "%)",
                SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(passed ? new Color(40, 140, 60) : new Color(190, 50, 50));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard(student);
        });

        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));
        panel.add(circle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scoreLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(backBtn);

        add(panel);
        setVisible(true);
    }
}
