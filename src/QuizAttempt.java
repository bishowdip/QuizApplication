import java.sql.Timestamp;

/**
 * QuizAttempt represents a user's attempt at taking a quiz.
 */
public class QuizAttempt {
    private int id;
    private int userId;
    private int quizId;
    private String quizTitle;
    private int score;
    private int totalMarks;
    private double percentage;
    private Timestamp completedAt;

    public QuizAttempt(int id, int userId, int quizId, String quizTitle, int score, int totalMarks, double percentage, Timestamp completedAt) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.completedAt = completedAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public int getScore() {
        return score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public String getGrade() {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }
}
