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
    private JTextField signupFirstNameField;
    private JTextField signupLastNameField;
    private JTextField signupEmailField;
    private JTextField signupPhoneField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
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
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Card panel for login/signup
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        
        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createSignupPanel(), "SIGNUP");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        // Login Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Username field
        usernameField = createTextField("Enter your username or email");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JLabel usernameLabel = new JLabel("Username or Email");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.addActionListener(e -> attemptLogin());

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));

        // Remember me and Forgot password
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JCheckBox rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rememberMeCheck.setForeground(TEXT_PRIMARY);
        rememberMeCheck.setBackground(Color.WHITE);
        rememberMeCheck.setFocusPainted(false);
        
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordBtn.setForeground(PRIMARY_BLUE);
        forgotPasswordBtn.setBackground(Color.WHITE);
        forgotPasswordBtn.setBorderPainted(false);
        forgotPasswordBtn.setFocusPainted(false);
        forgotPasswordBtn.setContentAreaFilled(false);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Default admin credentials:\nEmail: admin@vms.com\nPassword: admin123",
                "Password Help",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        optionsPanel.add(rememberMeCheck, BorderLayout.WEST);
        optionsPanel.add(forgotPasswordBtn, BorderLayout.EAST);
        
        formPanel.add(optionsPanel);
        formPanel.add(Box.createVerticalStrut(25));

        // Login button - full width
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
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
        formPanel.add(Box.createVerticalStrut(20));
        
        // Switch to signup
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switchPanel.setBackground(Color.WHITE);
        switchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel switchLabel = new JLabel("Don't have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        switchLabel.setForeground(TEXT_SECONDARY);
        
        JButton switchButton = new JButton("Register as Volunteer");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        signupFirstNameField = createTextField("First Name");
        signupLastNameField = createTextField("Last Name");
        signupEmailField = createTextField("Email");
        signupPhoneField = createTextField("Phone");
        signupPasswordField = new JPasswordField();
        signupPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signupPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        signupConfirmPasswordField = new JPasswordField();
        signupConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signupConfirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));

        addFormField(formPanel, "First Name", signupFirstNameField);
        addFormField(formPanel, "Last Name", signupLastNameField);
        addFormField(formPanel, "Email", signupEmailField);
        addFormField(formPanel, "Phone", signupPhoneField);
        addFormField(formPanel, "Password", signupPasswordField);
        addFormField(formPanel, "Confirm Password", signupConfirmPasswordField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton signupButton = new JButton("Create Account");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(PRIMARY_BLUE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setPreferredSize(new Dimension(150, 40));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Back to Login");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(TEXT_SECONDARY);
        cancelButton.setBackground(GRAY_BG);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(150, 40));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signupButton.addActionListener(e -> attemptSignup());
        // Instead of closing the dialog, go back to the main login view
        cancelButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Switch to login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setBackground(Color.WHITE);
        JLabel switchLabel = new JLabel("Already have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        switchLabel.setForeground(TEXT_SECONDARY);
        JButton switchButton = new JButton("Sign In");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        switchButton.setForeground(PRIMARY_BLUE);
        switchButton.setBackground(Color.WHITE);
        switchButton.setBorderPainted(false);
        switchButton.setFocusPainted(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);
        formPanel.add(switchPanel);

        // Make the whole signup form scrollable so all fields are accessible on smaller screens
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }
    
    private void addFormField(JPanel panel, String label, JComponent field) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldLabel.setForeground(TEXT_PRIMARY);
        panel.add(fieldLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(12, 12, 12, 12)
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
        String firstName = signupFirstNameField.getText().trim();
        String lastName = signupLastNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String phone = signupPhoneField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(signupConfirmPasswordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields.",
                "Signup Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Signup Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters long.",
                "Signup Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authService.register(firstName, lastName, email, phone, password)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully! You can now sign in.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            // Switch to login panel
            cardLayout.show(cardPanel, "LOGIN");
            usernameField.setText(email);
        } else {
            JOptionPane.showMessageDialog(this,
                "Email already exists. Please use a different email.",
                "Signup Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
