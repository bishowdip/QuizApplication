import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * QuizTakerPanel provides GUI for taking a quiz with result persistence.
 */
public class QuizTakerPanel extends JPanel {
    private Quiz quiz;
    private int currentQuestionIndex;
    private int[] userAnswers;
    private JLabel questionNumberLabel;
    private JLabel questionTextLabel;
    private ButtonGroup choiceGroup;
    private JRadioButton[] choiceButtons;
    private JButton previousButton;
    private JButton nextButton;
    private JButton finishButton;
    private JLabel marksLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private QuizApp parentApp;
    private JButton[] questionButtons; // Store question number buttons

    public QuizTakerPanel(Quiz quiz, QuizApp parentApp) {
        this.quiz = quiz;
        this.parentApp = parentApp;
        this.currentQuestionIndex = 0;
        this.userAnswers = new int[quiz.getQuestionCount()];
        this.questionButtons = new JButton[quiz.getQuestionCount()];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1; // -1 means no answer selected
        }
        initializeUI();
        displayQuestion(currentQuestionIndex);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 250));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Question panel
        JPanel questionPanel = createQuestionPanel();
        add(questionPanel, BorderLayout.CENTER);

        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Quiz title
        JLabel titleLabel = new JLabel(quiz.getTitle(), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        // Info panel (marks and progress)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setOpaque(false);

        marksLabel = new JLabel("Total: " + quiz.getTotalMarks() + " marks | " + quiz.getMarksPerQuestion() + " marks per question", SwingConstants.RIGHT);
        marksLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        marksLabel.setForeground(Color.WHITE);
        infoPanel.add(marksLabel);

        // Progress bar
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        progressPanel.setOpaque(false);

        progressLabel = new JLabel("Progress: 0/" + quiz.getQuestionCount());
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        progressLabel.setForeground(Color.WHITE);
        progressPanel.add(progressLabel);

        progressBar = new JProgressBar(0, quiz.getQuestionCount());
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(150, 15));
        progressBar.setStringPainted(false);
        progressPanel.add(progressBar);

        infoPanel.add(progressPanel);
        panel.add(infoPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Question number and text panel
        JPanel questionHeaderPanel = new JPanel(new BorderLayout(10, 15));
        questionHeaderPanel.setBackground(Color.WHITE);

        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionNumberLabel.setForeground(new Color(63, 81, 181));
        questionHeaderPanel.add(questionNumberLabel, BorderLayout.NORTH);

        questionTextLabel = new JLabel();
        questionTextLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionTextLabel.setVerticalAlignment(SwingConstants.TOP);
        questionHeaderPanel.add(questionTextLabel, BorderLayout.CENTER);

        panel.add(questionHeaderPanel, BorderLayout.NORTH);

        // Choices panel
        JPanel choicesPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        choicesPanel.setBackground(Color.WHITE);
        choicesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        choiceGroup = new ButtonGroup();
        choiceButtons = new JRadioButton[4];

        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            choiceButtons[i] = new JRadioButton();
            choiceButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            choiceButtons[i].setBackground(Color.WHITE);
            choiceButtons[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            choiceButtons[i].setFocusPainted(false);

            final int choiceIndex = i;
            choiceButtons[i].addActionListener(e -> {
                userAnswers[currentQuestionIndex] = choiceIndex;
                updateProgress();
                updateQuestionButtons(); // Update button colors when answer is selected
            });

            choiceGroup.add(choiceButtons[i]);
            choicesPanel.add(choiceButtons[i]);
        }

        panel.add(choicesPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 250));

        // Question navigator (numbered buttons)
        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        dotsPanel.setBackground(new Color(245, 245, 250));
        dotsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        for (int i = 0; i < quiz.getQuestionCount(); i++) {
            JButton dotButton = new JButton(String.valueOf(i + 1));
            dotButton.setPreferredSize(new Dimension(40, 40));
            dotButton.setFont(new Font("Arial", Font.BOLD, 12));
            dotButton.setFocusPainted(false);
            dotButton.setOpaque(true);
            dotButton.setBorderPainted(false);
            dotButton.putClientProperty("JButton.buttonType", "square");

            // Initial color - gray for unanswered
            dotButton.setBackground(UIHelper.UNANSWERED_QUESTION);
            dotButton.setForeground(Color.DARK_GRAY);

            final int index = i;
            dotButton.addActionListener(e -> {
                currentQuestionIndex = index;
                displayQuestion(currentQuestionIndex);
            });

            // Add hover effect for question buttons
            addQuestionButtonHoverEffect(dotButton, index);

            questionButtons[i] = dotButton;
            dotsPanel.add(dotButton);
        }

        JScrollPane dotsScroll = new JScrollPane(dotsPanel);
        dotsScroll.setBorder(null);
        dotsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dotsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        dotsScroll.setPreferredSize(new Dimension(0, 55));
        panel.add(dotsScroll, BorderLayout.NORTH);

        // Navigation buttons
        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navButtonsPanel.setBackground(new Color(245, 245, 250));

        previousButton = UIHelper.createStyledButton("< Previous", new Color(100, 100, 100), Color.WHITE, new Dimension(130, 45));
        previousButton.addActionListener(e -> previousQuestion());
        navButtonsPanel.add(previousButton);

        nextButton = UIHelper.createStyledButton("Next >", new Color(100, 100, 100), Color.WHITE, new Dimension(130, 45));
        nextButton.addActionListener(e -> nextQuestion());
        navButtonsPanel.add(nextButton);

        finishButton = UIHelper.createStyledButton("Finish Quiz", UIHelper.DANGER_RED, Color.WHITE, new Dimension(150, 45));
        finishButton.addActionListener(e -> finishQuiz());
        navButtonsPanel.add(finishButton);

        panel.add(navButtonsPanel, BorderLayout.CENTER);

        // Back to dashboard button
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exitPanel.setBackground(new Color(245, 245, 250));

        JButton exitButton = UIHelper.createStyledButton("Exit Quiz", new Color(120, 120, 120), Color.WHITE, 12, null);
        exitButton.addActionListener(e -> exitQuiz());
        exitPanel.add(exitButton);

        panel.add(exitPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Add hover effect for question number buttons
     */
    private void addQuestionButtonHoverEffect(JButton button, int questionIndex) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (questionIndex != currentQuestionIndex) {
                    // Darken current color on hover
                    Color currentColor = getQuestionButtonColor(questionIndex);
                    button.setBackground(UIHelper.darkenColor(currentColor, 0.15f));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Restore original color based on state
                updateSingleQuestionButton(questionIndex);
            }
        });
    }

    /**
     * Get the appropriate color for a question button based on its state
     */
    private Color getQuestionButtonColor(int index) {
        if (index == currentQuestionIndex) {
            return UIHelper.CURRENT_QUESTION; // Blue for current
        } else if (userAnswers[index] != -1) {
            return UIHelper.ANSWERED_QUESTION; // Green for answered
        } else {
            return UIHelper.UNANSWERED_QUESTION; // Gray for unanswered
        }
    }

    /**
     * Update a single question button's appearance
     */
    private void updateSingleQuestionButton(int index) {
        if (questionButtons[index] != null) {
            Color bgColor = getQuestionButtonColor(index);
            questionButtons[index].setBackground(bgColor);

            // Set text color based on background
            if (index == currentQuestionIndex || userAnswers[index] != -1) {
                questionButtons[index].setForeground(Color.WHITE);
            } else {
                questionButtons[index].setForeground(Color.DARK_GRAY);
            }
        }
    }

    /**
     * Update all question button colors
     */
    private void updateQuestionButtons() {
        for (int i = 0; i < questionButtons.length; i++) {
            updateSingleQuestionButton(i);
        }
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= quiz.getQuestionCount()) {
            return;
        }

        Question question = quiz.getQuestions().get(index);

        // Update question number and text
        questionNumberLabel.setText("Question " + (index + 1) + " of " + quiz.getQuestionCount());
        questionTextLabel.setText("<html><div style='width:500px;'>" + question.getQuestionText() + "</div></html>");

        // Update choices
        String[] choices = question.getChoices();
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            choiceButtons[i].setText(letters[i] + ". " + choices[i]);
        }

        // Restore user's previous selection if any
        choiceGroup.clearSelection();
        if (userAnswers[index] != -1) {
            choiceButtons[userAnswers[index]].setSelected(true);
        }

        // Update navigation buttons
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < quiz.getQuestionCount() - 1);

        // Update question number buttons to highlight current
        updateQuestionButtons();

        // Update progress
        updateProgress();
    }

    private void updateProgress() {
        int answered = 0;
        for (int answer : userAnswers) {
            if (answer != -1) answered++;
        }
        progressBar.setValue(answered);
        progressLabel.setText("Progress: " + answered + "/" + quiz.getQuestionCount());
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < quiz.getQuestionCount() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void exitQuiz() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit? Your progress will be lost.",
            "Exit Quiz", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            parentApp.returnToDashboard();
        }
    }

    private void finishQuiz() {
        // Check for unanswered questions
        int unanswered = 0;
        StringBuilder unansweredList = new StringBuilder();
        for (int i = 0; i < userAnswers.length; i++) {
            if (userAnswers[i] == -1) {
                unanswered++;
                if (unansweredList.length() > 0) unansweredList.append(", ");
                unansweredList.append(i + 1);
            }
        }

        if (unanswered > 0) {
            int response = JOptionPane.showConfirmDialog(this,
                "You have " + unanswered + " unanswered question(s): " + unansweredList + "\n\nDo you want to finish anyway?",
                "Incomplete Quiz",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Calculate score
        int score = quiz.calculateScore(userAnswers);
        double percentage = (double) score / quiz.getTotalMarks() * 100;

        // Save attempt to database
        User currentUser = parentApp.getCurrentUser();
        if (currentUser != null && quiz.getId() > 0) {
            DatabaseManager.getInstance().saveQuizAttempt(
                currentUser.getId(),
                quiz.getId(),
                score,
                quiz.getTotalMarks(),
                percentage,
                userAnswers,
                quiz
            );
        }

        // Show results
        showResults(score, percentage);
    }

    private void showResults(int score, double percentage) {
        // Create results dialog
        JDialog resultsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Quiz Results", true);
        resultsDialog.setLayout(new BorderLayout());
        resultsDialog.setSize(850, 650);
        resultsDialog.setLocationRelativeTo(this);

        JPanel resultsPanel = new JPanel(new BorderLayout(15, 15));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resultsPanel.setBackground(new Color(245, 245, 250));

        // Score header
        JPanel scorePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel congratsLabel = new JLabel("Quiz Completed!", SwingConstants.CENTER);
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        congratsLabel.setForeground(new Color(63, 81, 181));
        scorePanel.add(congratsLabel);

        JLabel scoreLabel = new JLabel("Your Score: " + score + " / " + quiz.getTotalMarks(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        scoreLabel.setForeground(getScoreColor(percentage));
        scorePanel.add(scoreLabel);

        String grade = getGrade(percentage);
        JLabel gradeLabel = new JLabel(String.format("%.1f%% - Grade: %s", percentage, grade), SwingConstants.CENTER);
        gradeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gradeLabel.setForeground(getScoreColor(percentage));
        scorePanel.add(gradeLabel);

        resultsPanel.add(scorePanel, BorderLayout.NORTH);

        // Detailed results table
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel detailsTitle = new JLabel("Detailed Results");
        detailsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        detailsPanel.add(detailsTitle, BorderLayout.NORTH);

        String[] columnNames = {"#", "Question", "Your Answer", "Correct Answer", "Status", "Marks"};
        Object[][] data = new Object[quiz.getQuestionCount()][6];

        int marksPerQuestion = quiz.getMarksPerQuestion();
        for (int i = 0; i < quiz.getQuestionCount(); i++) {
            Question q = quiz.getQuestions().get(i);
            String yourAnswer = userAnswers[i] == -1 ? "Not answered" : q.getChoices()[userAnswers[i]];
            String correctAnswer = q.getChoices()[q.getCorrectAnswerIndex()];
            boolean isCorrect = userAnswers[i] != -1 && q.isCorrect(userAnswers[i]);

            data[i][0] = i + 1;
            data[i][1] = q.getQuestionText().length() > 50 ?
                q.getQuestionText().substring(0, 47) + "..." : q.getQuestionText();
            data[i][2] = yourAnswer.length() > 30 ? yourAnswer.substring(0, 27) + "..." : yourAnswer;
            data[i][3] = correctAnswer.length() > 30 ? correctAnswer.substring(0, 27) + "..." : correctAnswer;
            data[i][4] = isCorrect ? "Correct" : "Wrong";
            data[i][5] = isCorrect ? marksPerQuestion : 0;
        }

        JTable resultsTable = new JTable(data, columnNames);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setRowHeight(28);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultsTable.setEnabled(false);

        // Column widths
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(50);

        detailsPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        resultsPanel.add(detailsPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 250));

        JButton retryButton = UIHelper.createStyledButton("Try Again", UIHelper.WARNING_ORANGE, Color.WHITE, new Dimension(140, 40));
        retryButton.addActionListener(e -> {
            resultsDialog.dispose();
            Quiz reloadedQuiz = DatabaseManager.getInstance().loadQuiz(quiz.getId());
            if (reloadedQuiz != null) {
                parentApp.showQuizTaker(reloadedQuiz);
            }
        });
        buttonPanel.add(retryButton);

        JButton dashboardButton = UIHelper.createStyledButton("Back to Dashboard", UIHelper.PRIMARY_BLUE, Color.WHITE, new Dimension(180, 40));
        dashboardButton.addActionListener(e -> {
            resultsDialog.dispose();
            parentApp.returnToDashboard();
        });
        buttonPanel.add(dashboardButton);

        JButton exitButton = UIHelper.createStyledButton("Exit Application", new Color(100, 100, 100), Color.WHITE, new Dimension(160, 40));
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        resultsDialog.add(resultsPanel);
        resultsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        resultsDialog.setVisible(true);
    }

    private Color getScoreColor(double percentage) {
        if (percentage >= 80) return new Color(46, 125, 50);  // Green
        if (percentage >= 60) return new Color(255, 152, 0);  // Orange
        return new Color(211, 47, 47);  // Red
    }

    private String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }
}
