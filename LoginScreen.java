import java.awt.*;
import javax.swing.*;

public class LoginScreen extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    public LoginScreen() {
        setTitle("Quiz Management System - Login");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Quiz Management System", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(heading, gc);
        gc.gridwidth = 1;

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        gc.gridy = 1; gc.gridx = 0; panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1; panel.add(usernameField, gc);

        gc.gridy = 2; gc.gridx = 0; panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1; panel.add(passwordField, gc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create an account");

        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(loginBtn, gc);
        gc.gridy = 4; panel.add(registerBtn, gc);
        gc.gridy = 5; panel.add(statusLabel, gc);

        add(panel);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Enter both username and password.");
                return;
            }
            User user = userDAO.login(username, password);
            if (user == null) {
                statusLabel.setText("Invalid username or password.");
                return;
            }
            dispose();
            if (user.isTeacher()) {
                new TeacherDashboard(user);
            } else {
                new StudentDashboard(user);
            }
        });

        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterScreen();
        });

        getRootPane().setDefaultButton(loginBtn);
        setVisible(true);
    }
}
