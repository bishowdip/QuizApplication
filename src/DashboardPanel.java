import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel is the main hub after login.
 * Shows available quizzes, user history, and navigation options.
 */
public class DashboardPanel extends JPanel {
    private QuizApp parentApp;
    private User currentUser;
    private JTable quizzesTable;
    private JTable historyTable;
    private DefaultTableModel quizzesTableModel;
    private DefaultTableModel historyTableModel;
    private JLabel welcomeLabel;
    private JLabel statsLabel;

    public DashboardPanel(QuizApp parentApp, User user) {
        this.parentApp = parentApp;
        this.currentUser = user;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content - split into left (quizzes) and right (history)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(500);

        // Left panel - Available Quizzes
        JPanel quizzesPanel = createQuizzesPanel();
        splitPane.setLeftComponent(quizzesPanel);

        // Right panel - My History
        JPanel historyPanel = createHistoryPanel();
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel - Action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);

        // Stats
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setForeground(Color.WHITE);
        panel.add(statsLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = UIHelper.createStyledButton("Logout", UIHelper.DANGER_RED, Color.WHITE, 12, null);
        logoutButton.addActionListener(e -> parentApp.logout());
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title
        JLabel title = new JLabel("Available Quizzes");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Questions", "Created By", "Your Best"};
        quizzesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quizzesTable = new JTable(quizzesTableModel);
        quizzesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        quizzesTable.setRowHeight(30);
        quizzesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        quizzesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide ID column but keep data
        quizzesTable.getColumnModel().getColumn(0).setMinWidth(0);
        quizzesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        quizzesTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(quizzesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton takeQuizButton = UIHelper.createStyledButton("Take Selected Quiz", UIHelper.SUCCESS_GREEN, Color.WHITE, 13, null);
        takeQuizButton.addActionListener(e -> takeSelectedQuiz());
        buttonPanel.add(takeQuizButton);

        JButton viewLeaderboardButton = UIHelper.createStyledButton("View Leaderboard", UIHelper.WARNING_ORANGE, Color.WHITE, 13, null);
        viewLeaderboardButton.addActionListener(e -> viewLeaderboard());
        buttonPanel.add(viewLeaderboardButton);

        JButton refreshButton = UIHelper.createStyledButton("Refresh", new Color(100, 100, 100), Color.WHITE, 13, null);
        refreshButton.addActionListener(e -> loadData());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title
        JLabel title = new JLabel("My Quiz History");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"Quiz", "Score", "Percentage", "Grade", "Date"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align percentage and grade columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        historyTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(245, 245, 250));

        JButton createQuizButton = UIHelper.createStyledButton("Create New Quiz", UIHelper.PRIMARY_BLUE, Color.WHITE, new Dimension(180, 45));
        createQuizButton.addActionListener(e -> parentApp.showQuizCreator());
        panel.add(createQuizButton);

        JButton myQuizzesButton = UIHelper.createStyledButton("My Created Quizzes", UIHelper.PURPLE, Color.WHITE, new Dimension(180, 45));
        myQuizzesButton.addActionListener(e -> showMyQuizzes());
        panel.add(myQuizzesButton);

        return panel;
    }

    public void loadData() {
        loadQuizzes();
        loadHistory();
        updateStats();
    }

    private void loadQuizzes() {
        quizzesTableModel.setRowCount(0);
        List<Quiz> quizzes = DatabaseManager.getInstance().getAllQuizzes();
        for (Quiz quiz : quizzes) {
            Quiz fullQuiz = DatabaseManager.getInstance().loadQuiz(quiz.getId());
            QuizAttempt bestAttempt = DatabaseManager.getInstance().getBestAttempt(currentUser.getId(), quiz.getId());
            String bestScore = bestAttempt != null ?
                String.format("%d/%d (%.0f%%)", bestAttempt.getScore(), bestAttempt.getTotalMarks(), bestAttempt.getPercentage()) :
                "Not attempted";

            quizzesTableModel.addRow(new Object[]{
                quiz.getId(),
                quiz.getTitle(),
                fullQuiz != null ? fullQuiz.getQuestionCount() : 0,
                quiz.getCreatorName() != null ? quiz.getCreatorName() : "Unknown",
                bestScore
            });
        }
    }

    private void loadHistory() {
        historyTableModel.setRowCount(0);
        List<QuizAttempt> attempts = DatabaseManager.getInstance().getAttemptsByUser(currentUser.getId());
        for (QuizAttempt attempt : attempts) {
            historyTableModel.addRow(new Object[]{
                attempt.getQuizTitle(),
                attempt.getScore() + "/" + attempt.getTotalMarks(),
                String.format("%.1f%%", attempt.getPercentage()),
                attempt.getGrade(),
                attempt.getCompletedAt().toString().substring(0, 16)
            });
        }
    }

    private void updateStats() {
        List<QuizAttempt> attempts = DatabaseManager.getInstance().getAttemptsByUser(currentUser.getId());
        int totalAttempts = attempts.size();
        double avgScore = attempts.stream().mapToDouble(QuizAttempt::getPercentage).average().orElse(0);
        statsLabel.setText(String.format("  |  Total Attempts: %d  |  Average Score: %.1f%%", totalAttempts, avgScore));
    }

    private void takeSelectedQuiz() {
        int selectedRow = quizzesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz to take!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quizId = (int) quizzesTableModel.getValueAt(selectedRow, 0);
        Quiz quiz = DatabaseManager.getInstance().loadQuiz(quizId);
        if (quiz != null && quiz.getQuestionCount() > 0) {
            parentApp.showQuizTaker(quiz);
        } else {
            JOptionPane.showMessageDialog(this, "This quiz has no questions!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewLeaderboard() {
        int selectedRow = quizzesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a quiz to view its leaderboard!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quizId = (int) quizzesTableModel.getValueAt(selectedRow, 0);
        String quizTitle = (String) quizzesTableModel.getValueAt(selectedRow, 1);
        List<LeaderboardEntry> leaderboard = DatabaseManager.getInstance().getQuizLeaderboard(quizId);

        if (leaderboard.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No one has taken this quiz yet!", "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create leaderboard dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Leaderboard - " + quizTitle, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Top 10 Scores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Rank", "Player", "Best Score", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (LeaderboardEntry entry : leaderboard) {
            model.addRow(new Object[]{
                entry.getMedal(),
                entry.getUsername(),
                entry.getBestScore(),
                String.format("%.1f%%", entry.getBestPercentage())
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void showMyQuizzes() {
        List<Quiz> myQuizzes = DatabaseManager.getInstance().getQuizzesByUser(currentUser.getId());

        if (myQuizzes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You haven't created any quizzes yet!", "My Quizzes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "My Created Quizzes", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Title", "Questions", "Created"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Quiz quiz : myQuizzes) {
            Quiz fullQuiz = DatabaseManager.getInstance().loadQuiz(quiz.getId());
            model.addRow(new Object[]{
                quiz.getId(),
                quiz.getTitle(),
                fullQuiz != null ? fullQuiz.getQuestionCount() : 0,
                quiz.getCreatedAt() != null ? quiz.getCreatedAt().toString().substring(0, 16) : "N/A"
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton deleteButton = UIHelper.createStyledButton("Delete Selected", UIHelper.DANGER_RED, Color.WHITE);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a quiz to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this quiz?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int quizId = (int) model.getValueAt(selectedRow, 0);
                if (DatabaseManager.getInstance().deleteQuiz(quizId)) {
                    model.removeRow(selectedRow);
                    loadData(); // Refresh dashboard
                }
            }
        });
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        loadData();
    }
}
