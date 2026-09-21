public class Result {
    private int id;
    private int quizId;
    private String quizTitle;
    private String subjectName;
    private int studentId;
    private String studentUsername;
    private int score;
    private int total;
    private String submittedAt;

    public Result(int id, int quizId, String quizTitle, String subjectName, int studentId,
                  String studentUsername, int score, int total, String submittedAt) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.subjectName = subjectName;
        this.studentId = studentId;
        this.studentUsername = studentUsername;
        this.score = score;
        this.total = total;
        this.submittedAt = submittedAt;
    }

    public int getId() { return id; }
    public int getQuizId() { return quizId; }
    public String getQuizTitle() { return quizTitle; }
    public String getSubjectName() { return subjectName; }
    public int getStudentId() { return studentId; }
    public String getStudentUsername() { return studentUsername; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getSubmittedAt() { return submittedAt; }

    public double getPercentage() {
        return total == 0 ? 0 : (score * 100.0) / total;
    }
}
