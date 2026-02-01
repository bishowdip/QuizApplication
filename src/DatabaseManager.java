import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles all database operations for the Quiz Application.
 * Uses SQLite for persistent storage of users, quizzes, questions, and results.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:quizapp.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    /**
     * Get singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Create all required tables
     */
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Users table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                email TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Quizzes table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS quizzes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                creator_id INTEGER,
                total_marks INTEGER DEFAULT 100,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (creator_id) REFERENCES users(id)
            )
        """);

        // Questions table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                quiz_id INTEGER NOT NULL,
                question_text TEXT NOT NULL,
                choice1 TEXT NOT NULL,
                choice2 TEXT NOT NULL,
                choice3 TEXT NOT NULL,
                choice4 TEXT NOT NULL,
                correct_answer_index INTEGER NOT NULL,
                marks INTEGER DEFAULT 0,
                question_order INTEGER,
                FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
            )
        """);

        // Quiz attempts/results table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS quiz_attempts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                quiz_id INTEGER NOT NULL,
                score INTEGER NOT NULL,
                total_marks INTEGER NOT NULL,
                percentage REAL NOT NULL,
                completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
            )
        """);

        // User answers table (for detailed results)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS user_answers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                attempt_id INTEGER NOT NULL,
                question_id INTEGER NOT NULL,
                selected_answer_index INTEGER,
                is_correct INTEGER,
                FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
                FOREIGN KEY (question_id) REFERENCES questions(id)
            )
        """);

        stmt.close();
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Register a new user
     */
    public boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user login
     */
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Simple password hashing (for demonstration - use bcrypt in production)
     */
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    // ==================== QUIZ OPERATIONS ====================

    /**
     * Save a quiz to database
     */
    public int saveQuiz(Quiz quiz, int creatorId) {
        String quizSql = "INSERT INTO quizzes (title, description, creator_id, total_marks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(quizSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getDescription());
            pstmt.setInt(3, creatorId);
            pstmt.setInt(4, quiz.getTotalMarks());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int quizId = generatedKeys.getInt(1);
                quiz.setId(quizId);

                // Save questions
                saveQuestions(quizId, quiz.getQuestions());
                return quizId;
            }
        } catch (SQLException e) {
            System.err.println("Save quiz error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Save questions for a quiz
     */
    private void saveQuestions(int quizId, List<Question> questions) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_text, choice1, choice2, choice3, choice4, correct_answer_index, marks, question_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String[] choices = q.getChoices();
                pstmt.setInt(1, quizId);
                pstmt.setString(2, q.getQuestionText());
                pstmt.setString(3, choices[0]);
                pstmt.setString(4, choices[1]);
                pstmt.setString(5, choices[2]);
                pstmt.setString(6, choices[3]);
                pstmt.setInt(7, q.getCorrectAnswerIndex());
                pstmt.setInt(8, q.getMarks());
                pstmt.setInt(9, i);
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    q.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Get all quizzes
     */
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.*, u.username as creator_name FROM quizzes q LEFT JOIN users u ON q.creator_id = u.id ORDER BY q.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz(rs.getString("title"));
                quiz.setId(rs.getInt("id"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCreatorName(rs.getString("creator_name"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Get quizzes error: " + e.getMessage());
        }
        return quizzes;
    }

    /**
     * Get quizzes created by a specific user
     */
    public List<Quiz> getQuizzesByUser(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE creator_id = ? ORDER BY created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz(rs.getString("title"));
                quiz.setId(rs.getInt("id"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Get user quizzes error: " + e.getMessage());
        }
        return quizzes;
    }

    /**
     * Load a complete quiz with questions
     */
    public Quiz loadQuiz(int quizId) {
        String quizSql = "SELECT q.*, u.username as creator_name FROM quizzes q LEFT JOIN users u ON q.creator_id = u.id WHERE q.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(quizSql)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz(rs.getString("title"));
                quiz.setId(rs.getInt("id"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCreatorName(rs.getString("creator_name"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));

                // Load questions
                loadQuestions(quiz);
                return quiz;
            }
        } catch (SQLException e) {
            System.err.println("Load quiz error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Load questions for a quiz
     */
    private void loadQuestions(Quiz quiz) throws SQLException {
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_order";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quiz.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] choices = {
                    rs.getString("choice1"),
                    rs.getString("choice2"),
                    rs.getString("choice3"),
                    rs.getString("choice4")
                };
                Question q = new Question(
                    rs.getString("question_text"),
                    choices,
                    rs.getInt("correct_answer_index"),
                    rs.getInt("marks")
                );
                q.setId(rs.getInt("id"));
                quiz.addQuestion(q);
            }
        }
    }

    /**
     * Delete a quiz
     */
    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Delete quiz error: " + e.getMessage());
            return false;
        }
    }

    // ==================== QUIZ ATTEMPT OPERATIONS ====================

    /**
     * Save a quiz attempt
     */
    public int saveQuizAttempt(int userId, int quizId, int score, int totalMarks, double percentage, int[] userAnswers, Quiz quiz) {
        String attemptSql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, total_marks, percentage) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(attemptSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            pstmt.setInt(3, score);
            pstmt.setInt(4, totalMarks);
            pstmt.setDouble(5, percentage);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int attemptId = generatedKeys.getInt(1);

                // Save individual answers
                saveUserAnswers(attemptId, userAnswers, quiz);
                return attemptId;
            }
        } catch (SQLException e) {
            System.err.println("Save attempt error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Save user answers for an attempt
     */
    private void saveUserAnswers(int attemptId, int[] userAnswers, Quiz quiz) throws SQLException {
        String sql = "INSERT INTO user_answers (attempt_id, question_id, selected_answer_index, is_correct) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            List<Question> questions = quiz.getQuestions();
            for (int i = 0; i < userAnswers.length; i++) {
                Question q = questions.get(i);
                pstmt.setInt(1, attemptId);
                pstmt.setInt(2, q.getId());
                pstmt.setInt(3, userAnswers[i]);
                pstmt.setInt(4, q.isCorrect(userAnswers[i]) ? 1 : 0);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Get quiz attempts by user
     */
    public List<QuizAttempt> getAttemptsByUser(int userId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = """
            SELECT qa.*, q.title as quiz_title
            FROM quiz_attempts qa
            JOIN quizzes q ON qa.quiz_id = q.id
            WHERE qa.user_id = ?
            ORDER BY qa.completed_at DESC
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                QuizAttempt attempt = new QuizAttempt(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("quiz_title"),
                    rs.getInt("score"),
                    rs.getInt("total_marks"),
                    rs.getDouble("percentage"),
                    rs.getTimestamp("completed_at")
                );
                attempts.add(attempt);
            }
        } catch (SQLException e) {
            System.err.println("Get attempts error: " + e.getMessage());
        }
        return attempts;
    }

    /**
     * Get best score for a user on a specific quiz
     */
    public QuizAttempt getBestAttempt(int userId, int quizId) {
        String sql = """
            SELECT qa.*, q.title as quiz_title
            FROM quiz_attempts qa
            JOIN quizzes q ON qa.quiz_id = q.id
            WHERE qa.user_id = ? AND qa.quiz_id = ?
            ORDER BY qa.score DESC LIMIT 1
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new QuizAttempt(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("quiz_title"),
                    rs.getInt("score"),
                    rs.getInt("total_marks"),
                    rs.getDouble("percentage"),
                    rs.getTimestamp("completed_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Get best attempt error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get leaderboard for a quiz
     */
    public List<LeaderboardEntry> getQuizLeaderboard(int quizId) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        String sql = """
            SELECT u.username, MAX(qa.score) as best_score, MAX(qa.percentage) as best_percentage
            FROM quiz_attempts qa
            JOIN users u ON qa.user_id = u.id
            WHERE qa.quiz_id = ?
            GROUP BY qa.user_id
            ORDER BY best_score DESC, best_percentage DESC
            LIMIT 10
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            int rank = 1;
            while (rs.next()) {
                leaderboard.add(new LeaderboardEntry(
                    rank++,
                    rs.getString("username"),
                    rs.getInt("best_score"),
                    rs.getDouble("best_percentage")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Get leaderboard error: " + e.getMessage());
        }
        return leaderboard;
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Close connection error: " + e.getMessage());
        }
    }
}
