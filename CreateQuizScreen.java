import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import java.text.SimpleDateFormat;

public class CreateQuizScreen extends JFrame {

    private final User teacher;
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final QuizDAO quizDAO = new QuizDAO();

    public CreateQuizScreen(User teacher) {
        this.teacher = teacher;

        setTitle("Create New Quiz");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;

        JLabel heading = new JLabel("Quiz Details", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(heading, gc);
        gc.gridwidth = 1;

        JTextField titleField = new JTextField(18);

        List<String> subjectNames = subjectDAO.getAllSubjectNames();
        JComboBox<String> subjectBox = new JComboBox<>(subjectNames.toArray(new String[0]));
        subjectBox.setEditable(true); // typing a new name creates a new subject

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 300, 1));

        // Deadline: date+time spinner, defaulting to one week from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        SpinnerDateModel dateModel = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        JSpinner deadlineSpinner = new JSpinner(dateModel);
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm"));

        JTextArea commentArea = new JTextArea(4, 18);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);

        gc.gridy = 1; gc.gridx = 0; panel.add(new JLabel("Title:"), gc);
        gc.gridx = 1; panel.add(titleField, gc);

        gc.gridy = 2; gc.gridx = 0; panel.add(new JLabel("Subject:"), gc);
        gc.gridx = 1; panel.add(subjectBox, gc);

        gc.gridy = 3; gc.gridx = 0; panel.add(new JLabel("Duration (min):"), gc);
        gc.gridx = 1; panel.add(durationSpinner, gc);

        gc.gridy = 4; gc.gridx = 0; panel.add(new JLabel("Deadline:"), gc);
        gc.gridx = 1; panel.add(deadlineSpinner, gc);

        gc.gridy = 5; gc.gridx = 0; panel.add(new JLabel("Comment:"), gc);
        gc.gridx = 1; panel.add(commentScroll, gc);

        JButton nextBtn = new JButton("Next: Add Questions \u2192");
        gc.gridy = 6; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(nextBtn, gc);

        add(panel);

        nextBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            Object subjectSelection = subjectBox.getEditor().getItem();
            String subjectName = subjectSelection == null ? "" : subjectSelection.toString().trim();
            int duration = (Integer) durationSpinner.getValue();
            Date deadlineDate = (Date) deadlineSpinner.getValue();
            String deadline = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(deadlineDate);
            String comment = commentArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a quiz title.");
                return;
            }
            if (subjectName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please choose or type a subject.");
                return;
            }

            int subjectId = subjectDAO.getOrCreateSubjectId(subjectName);
            int quizId = quizDAO.createQuiz(title, subjectId, teacher.getId(), duration, deadline, comment);
            if (quizId == -1) {
                JOptionPane.showMessageDialog(this, "Could not create the quiz. Please try again.");
                return;
            }
            dispose();
            new QuestionEditorScreen(teacher, quizId);
        });

        setVisible(true);
    }
}
