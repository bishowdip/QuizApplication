/**
 * Question class represents a single quiz question with four choices.
 * Supports database persistence with unique ID.
 */
public class Question {
    private int id;
    private String questionText;
    private String[] choices;
    private int correctAnswerIndex;
    private int marks;

    public Question(String questionText, String[] choices, int correctAnswerIndex, int marks) {
        this.id = -1; // Not saved to database yet
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }

    public String getCorrectAnswer() {
        return choices[correctAnswerIndex];
    }

    @Override
    public String toString() {
        return questionText;
    }
}
