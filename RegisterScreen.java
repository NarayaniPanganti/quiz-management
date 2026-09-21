import java.awt.*;
import javax.swing.*;

public class RegisterScreen extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    public RegisterScreen() {
        setTitle("Registration Form");
        setSize(380, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Create an account", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(heading, gc);
        gc.gridwidth = 1;

        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JRadioButton teacherOpt = new JRadioButton("Teacher");
        JRadioButton studentOpt = new JRadioButton("Student");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(teacherOpt);
        roleGroup.add(studentOpt);
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.add(teacherOpt);
        rolePanel.add(studentOpt);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        gc.gridy = 1; gc.gridx = 0; panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1; panel.add(nameField, gc);

        gc.gridy = 2; gc.gridx = 0; panel.add(new JLabel("Email:"), gc);
        gc.gridx = 1; panel.add(emailField, gc);

        gc.gridy = 3; gc.gridx = 0; panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1; panel.add(passField, gc);

        gc.gridy = 4; gc.gridx = 0; panel.add(new JLabel("Role:"), gc);
        gc.gridx = 1; panel.add(rolePanel, gc);

        JButton submitBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");

        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(submitBtn, gc);
        gc.gridy = 6; panel.add(backBtn, gc);
        gc.gridy = 7; panel.add(statusLabel, gc);

        add(panel);

        submitBtn.addActionListener(e -> {
            String username = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = teacherOpt.isSelected() ? "TEACHER" : studentOpt.isSelected() ? "STUDENT" : "";

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username and password are required.");
                return;
            }
            if (role.isEmpty()) {
                statusLabel.setText("Please select a role.");
                return;
            }

            String error = userDAO.register(username, email, password, role);
            if (error != null) {
                statusLabel.setText(error);
                return;
            }
            JOptionPane.showMessageDialog(this, "Account created! You can now log in.");
            dispose();
            new LoginScreen();
        });

        backBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        setVisible(true);
    }
}
