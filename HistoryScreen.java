import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HistoryScreen extends JFrame {

    private final User student;
    private JComboBox<String> subjectFilter;
    private DefaultTableModel model;
    private List<Result> allResults;

    public HistoryScreen(User student) {
        this.student = student;

        setTitle("My Quiz History - " + student.getUsername());
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        allResults = new ResultDAO().getResultsForStudent(student.getId());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Subject:"));
        java.util.LinkedHashSet<String> subjects = new java.util.LinkedHashSet<>();
        subjects.add("All Subjects");
        for (Result r : allResults) subjects.add(r.getSubjectName());
        subjectFilter = new JComboBox<>(subjects.toArray(new String[0]));
        topPanel.add(subjectFilter);

        String[] columns = {"Quiz", "Subject", "Score", "Percentage", "Submitted"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard(student);
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(backBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        subjectFilter.addActionListener(e -> refreshTable());
        refreshTable();

        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        String subject = (String) subjectFilter.getSelectedItem();
        for (Result r : allResults) {
            if ("All Subjects".equals(subject) || r.getSubjectName().equals(subject)) {
                model.addRow(new Object[]{
                        r.getQuizTitle(), r.getSubjectName(), r.getScore() + " / " + r.getTotal(),
                        String.format("%.1f%%", r.getPercentage()), r.getSubmittedAt()
                });
            }
        }
    }
}
