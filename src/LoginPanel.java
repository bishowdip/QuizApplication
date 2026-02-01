import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginPanel provides GUI for user authentication (login and registration).
 */
public class LoginPanel extends JPanel {
    private QuizApp parentApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton switchModeButton;
    private JLabel titleLabel;
    private JLabel emailLabel;
    private boolean isLoginMode = true;

    public LoginPanel(QuizApp parentApp) {
        this.parentApp = parentApp;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));

        // Main container with centered content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 245));

        // Login/Register card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        // App logo/title
        JLabel appTitle = new JLabel("Quiz Application");
        appTitle.setFont(new Font("Arial", Font.BOLD, 28));
        appTitle.setForeground(new Color(63, 81, 181));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(appTitle);

        cardPanel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Test your knowledge!");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);

        cardPanel.add(Box.createVerticalStrut(30));

        // Title label (Login/Register)
        titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createVerticalStrut(20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(usernameField, gbc);

        // Email (for registration only)
        gbc.gridx = 0; gbc.gridy = 1;
        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setVisible(false);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(200, 35));
        emailField.setVisible(false);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(passwordField, gbc);

        cardPanel.add(formPanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        loginButton = UIHelper.createStyledButton("Login", UIHelper.PRIMARY_BLUE, Color.WHITE, new Dimension(120, 40));
        loginButton.addActionListener(e -> handleLogin());
        buttonsPanel.add(loginButton);

        registerButton = UIHelper.createStyledButton("Register", UIHelper.SUCCESS_GREEN, Color.WHITE, new Dimension(120, 40));
        registerButton.setVisible(false);
        registerButton.addActionListener(e -> handleRegister());
        buttonsPanel.add(registerButton);

        cardPanel.add(buttonsPanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Switch mode link
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setBackground(Color.WHITE);

        JLabel switchLabel = new JLabel("Don't have an account?");
        switchLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        switchPanel.add(switchLabel);

        switchModeButton = new JButton("Register here");
        switchModeButton.setFont(new Font("Arial", Font.BOLD, 12));
        switchModeButton.setForeground(new Color(63, 81, 181));
        switchModeButton.setBorderPainted(false);
        switchModeButton.setContentAreaFilled(false);
        switchModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchModeButton.addActionListener(e -> toggleMode());
        switchPanel.add(switchModeButton);

        cardPanel.add(switchPanel);

        // Add card to center
        centerPanel.add(cardPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Add keyboard shortcuts
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isLoginMode) {
                        handleLogin();
                    } else {
                        handleRegister();
                    }
                }
            }
        });
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Login");
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            emailLabel.setVisible(false);
            emailField.setVisible(false);
            switchModeButton.setText("Register here");
            ((JLabel) ((JPanel) switchModeButton.getParent()).getComponent(0)).setText("Don't have an account?");
        } else {
            titleLabel.setText("Create Account");
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            emailLabel.setVisible(true);
            emailField.setVisible(true);
            switchModeButton.setText("Login here");
            ((JLabel) ((JPanel) switchModeButton.getParent()).getComponent(0)).setText("Already have an account?");
        }
        clearFields();
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields!");
            return;
        }

        User user = DatabaseManager.getInstance().authenticateUser(username, password);
        if (user != null) {
            parentApp.setCurrentUser(user);
            parentApp.showDashboard();
        } else {
            showError("Invalid username or password!");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required!");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters!");
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters!");
            return;
        }

        if (DatabaseManager.getInstance().usernameExists(username)) {
            showError("Username already exists!");
            return;
        }

        if (DatabaseManager.getInstance().registerUser(username, password, email)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! You can now login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            toggleMode(); // Switch to login mode
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
    }

    public void reset() {
        clearFields();
        if (!isLoginMode) {
            toggleMode();
        }
    }
}
