public class Question {
    private int id;
    private int quizId;
    private String questionText;
    private String[] choices; // exactly 4: A, B, C, D
    private int correctIndex; // 0-3

    public Question(int id, int quizId, String questionText, String[] choices, int correctIndex) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
    }

    /** Convenience constructor for a not-yet-saved question. */
    public Question(String questionText, String[] choices, int correctIndex) {
        this(-1, -1, questionText, choices, correctIndex);
    }

    public int getId() { return id; }
    public int getQuizId() { return quizId; }
    public String getQuestionText() { return questionText; }
    public String[] getChoices() { return choices; }
    public int getCorrectIndex() { return correctIndex; }
}
