import java.awt.*;
import java.util.List;
import javax.swing.*;

public class QuestionEditorScreen extends JFrame {

    private final User teacher;
    private final int quizId;
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final QuizDAO quizDAO = new QuizDAO();

    private final DefaultListModel<Question> listModel = new DefaultListModel<>();
    private final JList<Question> questionList = new JList<>(listModel);

    private final JTextField questionField = new JTextField(28);
    private final JTextField[] optionFields = new JTextField[4];
    private final JRadioButton[] correctButtons = new JRadioButton[4];
    private final ButtonGroup correctGroup = new ButtonGroup();

    private int editingQuestionId = -1; // -1 means "adding a new question"

    public QuestionEditorScreen(User teacher, int quizId) {
        this.teacher = teacher;
        this.quizId = quizId;

        Quiz quiz = quizDAO.getQuizById(quizId);
        setTitle("Questions - " + (quiz != null ? quiz.getTitle() : ""));
        setSize(760, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ----- left: list of questions already added -----
        questionList.setCellRenderer((list, value, index, isSelected, hasFocus) -> {
            JLabel label = new JLabel((index + 1) + ". " + value.getQuestionText());
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return label;
        });
        JScrollPane listScroll = new JScrollPane(questionList);
        listScroll.setPreferredSize(new Dimension(280, 400));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Questions in this quiz"));
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JPanel listButtons = new JPanel(new GridLayout(1, 2, 5, 5));
        listButtons.add(editBtn);
        listButtons.add(deleteBtn);
        leftPanel.add(listButtons, BorderLayout.SOUTH);

        // ----- right: form to add/edit a question -----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Question details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; formPanel.add(new JLabel("Question:"), gc);
        gc.gridx = 1; gc.gridwidth = 2; formPanel.add(questionField, gc);
        gc.gridwidth = 1;

        String[] optionLabels = {"Option A:", "Option B:", "Option C:", "Option D:"};
        for (int i = 0; i < 4; i++) {
            optionFields[i] = new JTextField(20);
            correctButtons[i] = new JRadioButton("Correct");
            correctGroup.add(correctButtons[i]);

            gc.gridy = i + 1; gc.gridx = 0;
            formPanel.add(new JLabel(optionLabels[i]), gc);
            gc.gridx = 1;
            formPanel.add(optionFields[i], gc);
            gc.gridx = 2;
            formPanel.add(correctButtons[i], gc);
        }

        JButton addUpdateBtn = new JButton("Add Question");
        JButton clearBtn = new JButton("Clear Form");
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 1;
        formPanel.add(addUpdateBtn, gc);
        gc.gridx = 1;
        formPanel.add(clearBtn, gc);

        // ----- bottom: save as draft / publish -----
        JButton saveDraftBtn = new JButton("Save as Draft");
        JButton publishBtn = new JButton("Publish Quiz");
        JButton backBtn = new JButton("Back to Dashboard");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.add(saveDraftBtn);
        bottomPanel.add(publishBtn);
        bottomPanel.add(backBtn);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(formPanel, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshList();

        // ----- actions -----
        addUpdateBtn.addActionListener(e -> {
            String text = questionField.getText().trim();
            String[] choices = new String[4];
            for (int i = 0; i < 4; i++) choices[i] = optionFields[i].getText().trim();
            int correctIndex = -1;
            for (int i = 0; i < 4; i++) if (correctButtons[i].isSelected()) correctIndex = i;

            if (text.isEmpty() || choices[0].isEmpty() || choices[1].isEmpty()
                    || choices[2].isEmpty() || choices[3].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in the question and all four options.");
                return;
            }
            if (correctIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please mark which option is correct.");
                return;
            }

            if (editingQuestionId == -1) {
                questionDAO.addQuestion(quizId, text, choices, correctIndex);
            } else {
                questionDAO.updateQuestion(editingQuestionId, text, choices, correctIndex);
            }
            clearForm();
            refreshList();
        });

        clearBtn.addActionListener(e -> clearForm());

        editBtn.addActionListener(e -> {
            Question selected = questionList.getSelectedValue();
            if (selected == null) return;
            editingQuestionId = selected.getId();
            questionField.setText(selected.getQuestionText());
            for (int i = 0; i < 4; i++) optionFields[i].setText(selected.getChoices()[i]);
            correctGroup.clearSelection();
            correctButtons[selected.getCorrectIndex()].setSelected(true);
            addUpdateBtn.setText("Update Question");
        });

        deleteBtn.addActionListener(e -> {
            Question selected = questionList.getSelectedValue();
            if (selected == null) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this question?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                questionDAO.deleteQuestion(selected.getId());
                clearForm();
                refreshList();
            }
        });

        saveDraftBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Saved as draft. You can keep editing it later from 'My Quizzes'.");
            dispose();
            new TeacherDashboard(teacher);
        });

        publishBtn.addActionListener(e -> {
            if (listModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Add at least one question before publishing.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Once published, students in this subject will be able to see and attempt this quiz. Continue?",
                    "Publish Quiz", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                quizDAO.publish(quizId);
                JOptionPane.showMessageDialog(this, "Quiz published!");
                dispose();
                new TeacherDashboard(teacher);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new TeacherDashboard(teacher);
        });

        setVisible(true);
    }

    private void clearForm() {
        editingQuestionId = -1;
        questionField.setText("");
        for (JTextField f : optionFields) f.setText("");
        correctGroup.clearSelection();
    }

    private void refreshList() {
        listModel.clear();
        List<Question> questions = questionDAO.getQuestionsForQuiz(quizId);
        for (Question q : questions) listModel.addElement(q);
    }
}
