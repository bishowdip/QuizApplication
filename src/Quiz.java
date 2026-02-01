import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz class manages a collection of questions.
 * Supports database persistence with unique ID.
 */
public class Quiz {
    private int id;
    private String title;
    private String description;
    private String creatorName;
    private Timestamp createdAt;
    private List<Question> questions;
    private static final int TOTAL_MARKS = 100;

    public Quiz(String title) {
        this.id = -1; // Not saved to database yet
        this.title = title;
        this.description = "";
        this.questions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        updateMarks();
    }

    public void removeQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            questions.remove(index);
            updateMarks();
        }
    }

    private void updateMarks() {
        if (!questions.isEmpty() && canDistributeMarksEqually()) {
            int marksPerQuestion = getMarksPerQuestion();
            for (Question q : questions) {
                q.setMarks(marksPerQuestion);
            }
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getTotalMarks() {
        return TOTAL_MARKS;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * Validates if marks can be distributed equally among questions
     * @return true if marks can be distributed as integers, false otherwise
     */
    public boolean canDistributeMarksEqually() {
        if (questions.isEmpty()) {
            return true;
        }
        return TOTAL_MARKS % questions.size() == 0;
    }

    /**
     * Gets marks per question
     * @return marks per question
     */
    public int getMarksPerQuestion() {
        if (questions.isEmpty()) {
            return 0;
        }
        return TOTAL_MARKS / questions.size();
    }

    /**
     * Calculates total score based on user answers
     * @param userAnswers array of user's selected answer indices
     * @return total score
     */
    public int calculateScore(int[] userAnswers) {
        if (userAnswers.length != questions.size()) {
            return 0;
        }

        int marksPerQuestion = getMarksPerQuestion();
        int score = 0;

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(userAnswers[i])) {
                score += marksPerQuestion;
            }
        }

        return score;
    }

    public boolean isSaved() {
        return id > 0;
    }

    @Override
    public String toString() {
        return title + " (" + questions.size() + " questions)";
    }
}
