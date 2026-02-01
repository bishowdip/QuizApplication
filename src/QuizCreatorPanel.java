import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * QuizCreatorPanel provides GUI for creating a quiz with database persistence.
 */
public class QuizCreatorPanel extends JPanel {
    private Quiz quiz;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField questionField;
    private JTextField[] choiceFields;
    private JComboBox<String> correctAnswerCombo;
    private DefaultTableModel tableModel;
    private JTable questionsTable;
    private JButton addQuestionButton;
    private JButton deleteQuestionButton;
    private JButton saveQuizButton;
    private JButton startQuizButton;
    private JButton backButton;
    private QuizApp parentApp;

    public QuizCreatorPanel(QuizApp parentApp) {
        this.parentApp = parentApp;
        this.quiz = new Quiz("My Quiz");
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 250));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(245, 245, 250));

        // Question input panel
        JPanel inputPanel = createQuestionInputPanel();

        // Questions table panel
        JPanel tablePanel = createQuestionsTablePanel();

        // Split pane for input and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, tablePanel);
        splitPane.setResizeWeight(0.45);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Back button
        backButton = new JButton("< Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> goBack());
        panel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel headerTitle = new JLabel("Create New Quiz", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 22));
        headerTitle.setForeground(Color.WHITE);
        panel.add(headerTitle, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuestionInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Quiz info panel (title + description)
        JPanel quizInfoPanel = new JPanel(new GridBagLayout());
        quizInfoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Quiz Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        quizInfoPanel.add(titleLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField("My Quiz", 30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        quizInfoPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        quizInfoPanel.add(descLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        descriptionArea = new JTextArea(2, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        quizInfoPanel.add(new JScrollPane(descriptionArea), gbc);

        panel.add(quizInfoPanel, BorderLayout.NORTH);

        // Question fields panel
        JPanel questionPanel = new JPanel(new GridBagLayout());
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Add Question"
        ));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Question text
        gbc.gridx = 0; gbc.gridy = 0;
        questionPanel.add(new JLabel("Question:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        questionField = new JTextField();
        questionField.setFont(new Font("Arial", Font.PLAIN, 13));
        questionPanel.add(questionField, gbc);

        // Four choices
        choiceFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.weightx = 0;
            questionPanel.add(new JLabel("Choice " + (i + 1) + ":"), gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            choiceFields[i] = new JTextField();
            choiceFields[i].setFont(new Font("Arial", Font.PLAIN, 13));
            questionPanel.add(choiceFields[i], gbc);
        }

        // Correct answer
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        questionPanel.add(new JLabel("Correct Answer:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        correctAnswerCombo = new JComboBox<>(new String[]{"Choice 1", "Choice 2", "Choice 3", "Choice 4"});
        questionPanel.add(correctAnswerCombo, gbc);

        // Add button
        gbc.gridx = 1; gbc.gridy = 6;
        addQuestionButton = UIHelper.createStyledButton("Add Question", UIHelper.SUCCESS_GREEN, Color.WHITE, 13, null);
        addQuestionButton.addActionListener(e -> addQuestion());
        questionPanel.add(addQuestionButton, gbc);

        panel.add(questionPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuestionsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title
        JLabel tableTitle = new JLabel("Questions List (" + quiz.getQuestionCount() + " questions)");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(tableTitle, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"#", "Question", "Correct Answer", "Marks"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(tableModel);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        questionsTable.setRowHeight(28);
        questionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Column widths
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(questionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Delete button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        deleteQuestionButton = UIHelper.createStyledButton("Delete Selected Question", UIHelper.DANGER_RED, Color.WHITE, 13, null);
        deleteQuestionButton.addActionListener(e -> deleteQuestion());
        buttonPanel.add(deleteQuestionButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(245, 245, 250));

        saveQuizButton = UIHelper.createStyledButton("Save Quiz to Database", UIHelper.PRIMARY_BLUE, Color.WHITE, new Dimension(200, 45));
        saveQuizButton.addActionListener(e -> saveQuiz());
        panel.add(saveQuizButton);

        startQuizButton = UIHelper.createStyledButton("Save & Take Quiz Now", UIHelper.DARK_GREEN, Color.WHITE, new Dimension(200, 45));
        startQuizButton.addActionListener(e -> saveAndStartQuiz());
        panel.add(startQuizButton);

        return panel;
    }

    private void addQuestion() {
        String questionText = questionField.getText().trim();
        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a question!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] choices = new String[4];
        for (int i = 0; i < 4; i++) {
            choices[i] = choiceFields[i].getText().trim();
            if (choices[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all choices!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int correctAnswerIndex = correctAnswerCombo.getSelectedIndex();

        // Add question to quiz
        Question question = new Question(questionText, choices, correctAnswerIndex, 0);
        quiz.addQuestion(question);

        // Update table
        updateTable();

        // Clear input fields
        questionField.setText("");
        for (JTextField field : choiceFields) {
            field.setText("");
        }
        correctAnswerCombo.setSelectedIndex(0);
        questionField.requestFocus();

        // Check if marks can be distributed equally
        if (!quiz.canDistributeMarksEqually()) {
            JOptionPane.showMessageDialog(this,
                "Note: " + quiz.getTotalMarks() + " marks cannot be distributed equally among " +
                quiz.getQuestionCount() + " questions.\nConsider adjusting the number of questions for even distribution.",
                "Mark Distribution Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this question?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            quiz.removeQuestion(selectedRow);
            updateTable();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        int marksPerQuestion = quiz.canDistributeMarksEqually() ? quiz.getMarksPerQuestion() : 0;

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);
            String correctChoice = q.getChoices()[q.getCorrectAnswerIndex()];
            tableModel.addRow(new Object[]{
                i + 1,
                q.getQuestionText(),
                correctChoice,
                marksPerQuestion > 0 ? marksPerQuestion : "N/A"
            });
        }

        // Update title panel with count
        updateTableTitle();
    }

    private void updateTableTitle() {
        // Find and update the table title label
        Component[] components = ((JPanel) questionsTable.getParent().getParent().getParent()).getComponents();
        for (Component c : components) {
            if (c instanceof JLabel) {
                ((JLabel) c).setText("Questions List (" + quiz.getQuestionCount() + " questions)");
                break;
            }
        }
    }

    private boolean validateQuiz() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quiz title!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (quiz.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one question!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!quiz.canDistributeMarksEqually()) {
            int response = JOptionPane.showConfirmDialog(this,
                "Marks cannot be distributed equally among " + quiz.getQuestionCount() + " questions.\n" +
                "Do you want to continue anyway?",
                "Mark Distribution Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            return response == JOptionPane.YES_OPTION;
        }

        return true;
    }

    private void saveQuiz() {
        if (!validateQuiz()) {
            return;
        }

        quiz.setTitle(titleField.getText().trim());
        quiz.setDescription(descriptionArea.getText().trim());

        int quizId = DatabaseManager.getInstance().saveQuiz(quiz, parentApp.getCurrentUser().getId());
        if (quizId > 0) {
            JOptionPane.showMessageDialog(this,
                "Quiz saved successfully!\nQuiz ID: " + quizId,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            parentApp.returnToDashboard();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to save quiz. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndStartQuiz() {
        if (!validateQuiz()) {
            return;
        }

        quiz.setTitle(titleField.getText().trim());
        quiz.setDescription(descriptionArea.getText().trim());

        int quizId = DatabaseManager.getInstance().saveQuiz(quiz, parentApp.getCurrentUser().getId());
        if (quizId > 0) {
            // Reload the quiz from database to get proper IDs
            Quiz savedQuiz = DatabaseManager.getInstance().loadQuiz(quizId);
            if (savedQuiz != null) {
                parentApp.showQuizTaker(savedQuiz);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to save quiz. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        if (quiz.getQuestionCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes. Are you sure you want to go back?",
                "Unsaved Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        parentApp.returnToDashboard();
    }

    public Quiz getQuiz() {
        return quiz;
    }
}
