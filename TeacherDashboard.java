import java.awt.*;
import javax.swing.*;

public class TeacherDashboard extends JFrame {

    private final User teacher;

    public TeacherDashboard(User teacher) {
        this.teacher = teacher;

        setTitle("Teacher Dashboard - " + teacher.getUsername());
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel heading = new JLabel("Welcome, " + teacher.getUsername());
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createQuizBtn = new JButton("Create New Quiz");
        JButton myQuizzesBtn = new JButton("My Quizzes (Drafts & Published)");
        JButton logoutBtn = new JButton("Logout");

        for (JButton b : new JButton[]{createQuizBtn, myQuizzesBtn, logoutBtn}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(300, 40));
        }

        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createQuizBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(myQuizzesBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(logoutBtn);

        add(panel);

        createQuizBtn.addActionListener(e -> new CreateQuizScreen(teacher));
        myQuizzesBtn.addActionListener(e -> new MyQuizzesScreen(teacher));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        setVisible(true);
    }
}
