public class Quiz {
    private int id;
    private String title;
    private int subjectId;
    private String subjectName;
    private int teacherId;
    private int durationMinutes;
    private String deadline;   // stored as "yyyy-MM-dd HH:mm"
    private String comment;
    private String status;     // "DRAFT" or "PUBLISHED"
    private String createdAt;
    private int questionCount; // convenience field, not always populated

    public Quiz(int id, String title, int subjectId, String subjectName, int teacherId,
                int durationMinutes, String deadline, String comment, String status, String createdAt) {
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.durationMinutes = durationMinutes;
        this.deadline = deadline;
        this.comment = comment;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public int getTeacherId() { return teacherId; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getDeadline() { return deadline; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public boolean isDraft() { return "DRAFT".equalsIgnoreCase(status); }
    public String getCreatedAt() { return createdAt; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    @Override
    public String toString() {
        return title + "  [" + subjectName + "]";
    }
}
