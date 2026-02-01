import javax.swing.*;
import java.awt.*;

/**
 * Main application class for Quiz Application.
 * This application allows users to create and take quizzes with persistent storage.
 *
 * Features:
 * 1. User authentication (login/register)
 * 2. Create quizzes with multiple questions
 * 3. Save quizzes to SQLite database
 * 4. Take quizzes created by any user
 * 5. Track quiz history and scores
 * 6. View leaderboards
 * 7. Navigate forward/backward through questions
 * 8. No time limit
 *
 * @author Quiz Application Team
 * @version 2.0
 */
public class QuizApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private QuizCreatorPanel creatorPanel;
    private QuizTakerPanel takerPanel;
    private User currentUser;

    // Panel names for CardLayout
    private static final String LOGIN_PANEL = "LOGIN";
    private static final String DASHBOARD_PANEL = "DASHBOARD";
    private static final String CREATOR_PANEL = "CREATOR";
    private static final String TAKER_PANEL = "TAKER";

    public QuizApp() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Use CardLayout to switch between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add login panel
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, LOGIN_PANEL);

        add(mainPanel);

        // Show login panel first
        cardLayout.show(mainPanel, LOGIN_PANEL);

        // Add shutdown hook to close database
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.getInstance().close();
        }));
    }

    /**
     * Set the current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Get the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Show the main dashboard after login
     */
    public void showDashboard() {
        if (dashboardPanel == null) {
            dashboardPanel = new DashboardPanel(this, currentUser);
            mainPanel.add(dashboardPanel, DASHBOARD_PANEL);
        } else {
            dashboardPanel.setCurrentUser(currentUser);
            dashboardPanel.loadData();
        }
        cardLayout.show(mainPanel, DASHBOARD_PANEL);
    }

    /**
     * Show quiz creator panel
     */
    public void showQuizCreator() {
        creatorPanel = new QuizCreatorPanel(this);
        mainPanel.add(creatorPanel, CREATOR_PANEL);
        cardLayout.show(mainPanel, CREATOR_PANEL);
    }

    /**
     * Switches to quiz taker panel to start taking the quiz
     * @param quiz The quiz to be taken
     */
    public void showQuizTaker(Quiz quiz) {
        takerPanel = new QuizTakerPanel(quiz, this);
        mainPanel.add(takerPanel, TAKER_PANEL);
        cardLayout.show(mainPanel, TAKER_PANEL);
    }

    /**
     * Logout and return to login screen
     */
    public void logout() {
        currentUser = null;
        loginPanel.reset();
        cardLayout.show(mainPanel, LOGIN_PANEL);
    }

    /**
     * Return to dashboard
     */
    public void returnToDashboard() {
        if (dashboardPanel != null) {
            dashboardPanel.loadData();
        }
        cardLayout.show(mainPanel, DASHBOARD_PANEL);
    }

    /**
     * Main method to launch the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize database
        DatabaseManager.getInstance();

        // Create and show the application
        SwingUtilities.invokeLater(() -> {
            QuizApp app = new QuizApp();
            app.setVisible(true);
        });
    }
}
