package com.fstgc.vms.ui;

import com.fstgc.vms.service.AuthenticationService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginDialog extends JDialog {
    private static final Color PRIMARY_BLUE = new Color(29, 78, 216);
    private static final Color GRAY_BG = new Color(249, 250, 251);
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField signupUsernameField;
    private JTextField signupFirstNameField;
    private JTextField signupLastNameField;
    private JTextField signupEmailField;
    private JTextField signupPhoneField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private JComboBox<String> signupSecurityQuestionCombo;
    private JTextField signupSecurityAnswerField;
    private AuthenticationService authService;
    private boolean authenticated = false;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public LoginDialog(Frame parent, AuthenticationService authService) {
        super(parent, "Login - Volunteer Management System", true);
        this.authService = authService;
        initializeUI();
    }

    private void initializeUI() {
        // Calculate appropriate size based on screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = Math.min(450, (int)(screenSize.width * 0.85));
        int dialogHeight = Math.min(580, (int)(screenSize.height * 0.80));
        
        setSize(dialogWidth, dialogHeight);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(400, 480));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Card panel for login/signup
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        
        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createSignupPanel(), "SIGNUP");

        mainPanel.add(cardPanel, BorderLayout.WEST);

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        // Login Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Username field
        usernameField = createTextField("Enter your username or email");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel usernameLabel = new JLabel("Username or Email");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(12));

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.addActionListener(e -> attemptLogin());

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passwordLabel.setForeground(TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));

        // Remember me and Forgot password
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JCheckBox rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rememberMeCheck.setForeground(TEXT_PRIMARY);
        rememberMeCheck.setBackground(Color.WHITE);
        rememberMeCheck.setFocusPainted(false);
        
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgotPasswordBtn.setForeground(PRIMARY_BLUE);
        forgotPasswordBtn.setBackground(Color.WHITE);
        forgotPasswordBtn.setBorderPainted(false);
        forgotPasswordBtn.setFocusPainted(false);
        forgotPasswordBtn.setContentAreaFilled(false);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordBtn.addActionListener(e -> showForgotPasswordDialog());
        
        optionsPanel.add(rememberMeCheck, BorderLayout.WEST);
        optionsPanel.add(forgotPasswordBtn, BorderLayout.EAST);
        
        formPanel.add(optionsPanel);
        formPanel.add(Box.createVerticalStrut(18));

        // Login button - full width
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> attemptLogin());
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(PRIMARY_BLUE);
            }
        });

        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Switch to signup
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switchPanel.setBackground(Color.WHITE);
        switchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel switchLabel = new JLabel("Don't have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        switchLabel.setForeground(TEXT_SECONDARY);
        
        JButton switchButton = new JButton("Register as Volunteer");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        switchButton.setForeground(PRIMARY_BLUE);
        switchButton.setBackground(Color.WHITE);
        switchButton.setBorderPainted(false);
        switchButton.setFocusPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> cardLayout.show(cardPanel, "SIGNUP"));
        
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);
        formPanel.add(switchPanel);

        return formPanel;
    }
    
    private JPanel createSignupPanel() {
        // Outer container so we can embed the form in a scroll pane
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Back button and Title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JButton backButton = new JButton("\u2190 Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        backButton.setForeground(PRIMARY_BLUE);
        backButton.setBackground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        headerPanel.add(backButton);
        
        formPanel.add(headerPanel);
        formPanel.add(Box.createVerticalStrut(3));
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(6));

        signupUsernameField = createTextField("Username");
        signupFirstNameField = createTextField("First Name");
        signupLastNameField = createTextField("Last Name");
        signupEmailField = createTextField("Email");
        signupPhoneField = createTextField("Phone");
        signupPasswordField = new JPasswordField();
        signupPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        signupPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(4, 6, 4, 6)
        ));
        signupConfirmPasswordField = new JPasswordField();
        signupConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        signupConfirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(4, 6, 4, 6)
        ));

        // Security question dropdown
        String[] securityQuestions = {
            "What is your favorite color?",
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What is your favorite book?"
        };
        signupSecurityQuestionCombo = new JComboBox<>(securityQuestions);
        signupSecurityQuestionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        signupSecurityQuestionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        signupSecurityQuestionCombo.setBackground(Color.WHITE);
        
        signupSecurityAnswerField = createTextField("Your answer");

        addFormField(formPanel, "Username", signupUsernameField);
        addFormField(formPanel, "First Name", signupFirstNameField);
        addFormField(formPanel, "Last Name", signupLastNameField);
        addFormField(formPanel, "Email", signupEmailField);
        addFormField(formPanel, "Phone", signupPhoneField);
        addFormField(formPanel, "Password", signupPasswordField);
        addFormField(formPanel, "Confirm Password", signupConfirmPasswordField);
        
        // Add security question section with header
        formPanel.add(Box.createVerticalStrut(8));
        JLabel securityHeader = new JLabel("Security Question (for password recovery)");
        securityHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        securityHeader.setForeground(TEXT_PRIMARY);
        securityHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(securityHeader);
        formPanel.add(Box.createVerticalStrut(6));
        
        addFormField(formPanel, "Security Question", signupSecurityQuestionCombo);
        addFormField(formPanel, "Security Answer", signupSecurityAnswerField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton signupButton = new JButton("Create Account");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(PRIMARY_BLUE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setPreferredSize(new Dimension(140, 35));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton backToLoginButton = new JButton("Back");
        backToLoginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backToLoginButton.setForeground(TEXT_SECONDARY);
        backToLoginButton.setBackground(GRAY_BG);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setPreferredSize(new Dimension(140, 35));
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signupButton.addActionListener(e -> attemptSignup());
        backToLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));

        buttonPanel.add(signupButton);
        buttonPanel.add(backToLoginButton);

        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Switch to login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setBackground(Color.WHITE);
        JLabel switchLabel = new JLabel("Already have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        switchLabel.setForeground(TEXT_SECONDARY);
        JButton switchButton = new JButton("Sign In");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        switchButton.setForeground(PRIMARY_BLUE);
        switchButton.setBackground(Color.WHITE);
        switchButton.setBorderPainted(false);
        switchButton.setFocusPainted(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);
        formPanel.add(switchPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }
    
    private void addFormField(JPanel panel, String label, JComponent field) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fieldLabel.setForeground(TEXT_PRIMARY);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fieldLabel);
        panel.add(Box.createVerticalStrut(2));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(6));
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(4, 6, 4, 6)
        ));
        // Add placeholder text
        field.setForeground(new Color(156, 163, 175));
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(new Color(156, 163, 175));
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Check if placeholder text is still showing
        if (username.isEmpty() || username.equals("Enter your username or email") || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Login Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authService.login(username, password)) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid credentials or account locked. Please try again.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void attemptSignup() {
        String username = signupUsernameField.getText().trim();
        String firstName = signupFirstNameField.getText().trim();
        String lastName = signupLastNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String phone = signupPhoneField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(signupConfirmPasswordField.getPassword());
        String securityQuestion = (String) signupSecurityQuestionCombo.getSelectedItem();
        String securityAnswer = signupSecurityAnswerField.getText().trim();

        // Check if all fields are filled
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || securityAnswer.isEmpty() || securityAnswer.equals("Your answer")) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields including security question.",
                "Signup Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.\nExample: user@example.com",
                "Invalid Email",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate phone format (allow only digits, spaces, hyphens, parentheses, and + sign)
        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid phone number.\nPhone should contain only digits, spaces, hyphens, parentheses, or + symbol.\nExample: 123-456-7890 or +1 (123) 456-7890",
                "Invalid Phone Number",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check password match (case-sensitive comparison)
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please ensure both password fields contain identical values.",
                "Password Mismatch",
                JOptionPane.WARNING_MESSAGE);
            signupPasswordField.setText("");
            signupConfirmPasswordField.setText("");
            signupPasswordField.requestFocus();
            return;
        }

        // Check password length
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters long.",
                "Weak Password",
                JOptionPane.WARNING_MESSAGE);
            signupPasswordField.setText("");
            signupConfirmPasswordField.setText("");
            signupPasswordField.requestFocus();
            return;
        }

        // Attempt registration
        if (authService.register(username, firstName, lastName, email, phone, password, securityQuestion, securityAnswer)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully! You can now sign in.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            // Switch to login panel
            cardLayout.show(cardPanel, "LOGIN");
            usernameField.setText(username);
            // Clear signup fields
            clearSignupFields();
        } else {
            JOptionPane.showMessageDialog(this,
                "Username or email already exists. Please use different credentials.",
                "Signup Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation pattern
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }

    private boolean isValidPhone(String phone) {
        // Phone can contain digits, spaces, hyphens, parentheses, and + sign
        // Must have at least 10 characters (excluding formatting)
        String phoneDigitsOnly = phone.replaceAll("[^0-9]", "");
        
        // Allow phone numbers with at least 10 digits
        if (phoneDigitsOnly.length() < 10) {
            return false;
        }
        
        // Check if phone contains only valid characters
        String validPhonePattern = "^[0-9\\s\\-()+]+$";
        return phone.matches(validPhonePattern);
    }

    private void clearSignupFields() {
        signupUsernameField.setText("");
        signupFirstNameField.setText("");
        signupLastNameField.setText("");
        signupEmailField.setText("");
        signupPhoneField.setText("");
        signupPasswordField.setText("");
        signupConfirmPasswordField.setText("");
        signupSecurityQuestionCombo.setSelectedIndex(0);
        signupSecurityAnswerField.setText("");
    }

    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Reset Password", true);
        dialog.setSize(480, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(12));

        JLabel instructionLabel = new JLabel("<html><center>Enter your username or email to reset your password.</center></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(TEXT_SECONDARY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Username/Email field
        JLabel usernameLabel = new JLabel("Username or Email");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JTextField usernameResetField = new JTextField();
        usernameResetField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameResetField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        usernameResetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        usernameResetField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(usernameResetField);
        mainPanel.add(Box.createVerticalStrut(25));

        // Next button
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(PRIMARY_BLUE);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        nextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> {
            String usernameOrEmail = usernameResetField.getText().trim();
            if (usernameOrEmail.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter your username or email.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String securityQuestion = authService.getSecurityQuestion(usernameOrEmail);
            if (securityQuestion == null) {
                JOptionPane.showMessageDialog(dialog,
                    "No account found with that username or email.",
                    "Account Not Found",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();
            showSecurityQuestionDialog(usernameOrEmail, securityQuestion);
        });

        mainPanel.add(nextButton);
        mainPanel.add(Box.createVerticalStrut(12));

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(TEXT_SECONDARY);
        cancelButton.setBackground(GRAY_BG);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        cancelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dialog.dispose());

        mainPanel.add(cancelButton);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showSecurityQuestionDialog(String usernameOrEmail, String securityQuestion) {
        JDialog dialog = new JDialog(this, "Answer Security Question", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Security Verification");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Security Question
        JLabel questionLabel = new JLabel("Security Question:");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionLabel.setForeground(TEXT_PRIMARY);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(questionLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JLabel questionText = new JLabel("<html>" + securityQuestion + "</html>");
        questionText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionText.setForeground(TEXT_SECONDARY);
        questionText.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(questionText);
        mainPanel.add(Box.createVerticalStrut(20));

        // Answer field
        JLabel answerLabel = new JLabel("Your Answer");
        answerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        answerLabel.setForeground(TEXT_PRIMARY);
        answerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(answerLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JTextField answerField = new JTextField();
        answerField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        answerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        answerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(answerField);
        mainPanel.add(Box.createVerticalStrut(20));

        // New Password field
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newPasswordLabel.setForeground(TEXT_PRIMARY);
        newPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(newPasswordLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(newPasswordField);
        mainPanel.add(Box.createVerticalStrut(18));

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm New Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordLabel.setForeground(TEXT_PRIMARY);
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(confirmPasswordLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(25));

        // Reset button
        JButton resetButton = new JButton("Reset Password");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(PRIMARY_BLUE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> {
            String answer = answerField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (answer.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all fields.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Passwords do not match.",
                    "Password Mismatch",
                    JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(dialog,
                    "Password must be at least 6 characters long.",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authService.resetPassword(usernameOrEmail, answer, newPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Password reset successfully! You can now login with your new password.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Incorrect security answer. Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE);
                answerField.setText("");
            }
        });

        mainPanel.add(resetButton);
        mainPanel.add(Box.createVerticalStrut(12));

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(TEXT_SECONDARY);
        cancelButton.setBackground(GRAY_BG);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        cancelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dialog.dispose());

        mainPanel.add(cancelButton);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
