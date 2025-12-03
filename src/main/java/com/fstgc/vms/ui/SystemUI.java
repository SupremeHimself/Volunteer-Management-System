package com.fstgc.vms.ui;

import com.fstgc.vms.controller.*;
import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.model.Event;

import com.fstgc.vms.model.SystemAdmin;
import com.fstgc.vms.model.enums.*;
import com.fstgc.vms.repository.memory.*;
import com.fstgc.vms.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SystemUI extends JFrame {
    private final VolunteerController volunteerController;
    private final EventController eventController;
    private final AttendanceController attendanceController;
    private final TimesheetController timesheetController;
    private final AnnouncementController announcementController;
    private final AuthenticationService authService;

    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    
    // Modern color palette
    private static final Color PRIMARY_BLUE = new Color(29, 78, 216);
    private static final Color LIGHT_BLUE = new Color(219, 234, 254);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color PURPLE = new Color(168, 85, 247);
    private static final Color ORANGE = new Color(249, 115, 22);
    private static final Color GRAY_BG = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    public SystemUI(AuthenticationService authService) {
        this.authService = authService;
        
        // Initialize services and controllers
        VolunteerService volunteerService = new VolunteerService(new InMemoryVolunteerRepository());
        InMemoryEventRepository eventRepository = new InMemoryEventRepository();
        EventService eventService = new EventService(eventRepository);
        AttendanceService attendanceService = new AttendanceService(new InMemoryAttendanceRepository(), eventRepository);
        TimesheetService timesheetService = new TimesheetService(new InMemoryTimesheetRepository(), new InMemoryAttendanceRepository());
        AnnouncementService announcementService = new AnnouncementService(new InMemoryAnnouncementRepository());

        this.volunteerController = new VolunteerController(volunteerService);
        this.eventController = new EventController(eventService);
        this.attendanceController = new AttendanceController(attendanceService);
        this.timesheetController = new TimesheetController(timesheetService);
        this.announcementController = new AnnouncementController(announcementService);

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Volunteer Management System - FST Guild Committee");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set overall background
        getContentPane().setBackground(GRAY_BG);

        // Create main container with gradient-like effect
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(GRAY_BG);
        
        // Create custom header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(GRAY_BG);
        tabbedPane.setFont(getEmojiFont(14));
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add tabs with icons
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("👥 Volunteers", createVolunteerPanel());
        tabbedPane.addTab("📅 Events", createEventPanel());
        tabbedPane.addTab("✓ Attendance", createAttendancePanel());
        tabbedPane.addTab("⏰ Timesheets", createTimesheetPanel());
        tabbedPane.addTab("🏆 Awards", createAwardPanel());
        tabbedPane.addTab("📢 Announcements", createAnnouncementPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void refreshAllPanels() {
        int currentTab = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(0, createDashboardPanel());
        tabbedPane.setComponentAt(1, createVolunteerPanel());
        tabbedPane.setComponentAt(2, createEventPanel());
        tabbedPane.setComponentAt(3, createAttendancePanel());
        tabbedPane.setComponentAt(4, createTimesheetPanel());
        tabbedPane.setComponentAt(5, createAwardPanel());
        tabbedPane.setComponentAt(6, createAnnouncementPanel());
        tabbedPane.setSelectedIndex(currentTab);
    }
    
    private void refreshDashboard() {
        tabbedPane.setComponentAt(0, createDashboardPanel());
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftSection.setBackground(PRIMARY_BLUE);
        
        JLabel titleLabel = new JLabel("Volunteer Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("FST Guild Committee");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(LIGHT_BLUE);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(PRIMARY_BLUE);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        leftSection.add(titlePanel);
        header.add(leftSection, BorderLayout.WEST);
        
        // Right section - User info and logout
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightSection.setBackground(PRIMARY_BLUE);
        
        SystemAdmin currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            JPanel userInfo = new JPanel();
            userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
            userInfo.setBackground(PRIMARY_BLUE);
            
            JLabel userNameLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
            userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            userNameLabel.setForeground(Color.WHITE);
            userNameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            JLabel userRoleLabel = new JLabel(currentUser.getRole().toString());
            userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            userRoleLabel.setForeground(LIGHT_BLUE);
            userRoleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            userInfo.add(userNameLabel);
            userInfo.add(userRoleLabel);
            
            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            logoutButton.setForeground(PRIMARY_BLUE);
            logoutButton.setBackground(Color.WHITE);
            logoutButton.setFocusPainted(false);
            logoutButton.setBorderPainted(false);
            logoutButton.setPreferredSize(new Dimension(80, 35));
            logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoutButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    authService.logout();
                    dispose();
                    System.exit(0);
                }
            });
            
            rightSection.add(userInfo);
            rightSection.add(logoutButton);
        }
        
        header.add(rightSection, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(31, 41, 55));
        footer.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel copyrightLabel = new JLabel("© 2025 Faculty of Science and Technology Guild Committee");
        copyrightLabel.setForeground(Color.WHITE);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        footer.add(copyrightLabel);
        return footer;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(GRAY_BG);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(GRAY_BG);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        List<Volunteer> volunteers = volunteerController.listAll();
        List<com.fstgc.vms.model.Event> events = eventController.listAll();
        int totalHours = volunteers.stream().mapToInt(v -> (int)v.getTotalHoursWorked()).sum();
        
        statsPanel.add(createStatCard("Active Volunteers", String.valueOf(volunteers.size()), PRIMARY_BLUE, "👥", 0));
        statsPanel.add(createStatCard("Upcoming Events", String.valueOf(events.size()), GREEN, "📅", 1));
        statsPanel.add(createStatCard("Total Hours", String.valueOf(totalHours), PURPLE, "⏰", 2));
        statsPanel.add(createStatCard("Badges Earned", "0", ORANGE, "🏆", 3));
        
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Announcements and Events section
        JPanel lowerSection = new JPanel(new GridLayout(1, 2, 15, 0));
        lowerSection.setBackground(GRAY_BG);
        
        lowerSection.add(createAnnouncementsPreview());
        lowerSection.add(createEventsPreview());
        
        contentPanel.add(lowerSection);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color, String emoji, int tabIndex) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 0),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(tabIndex);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(color);
            }
        });
        
        JLabel emojiLabel = new JLabel(emoji);
        // Try multiple fonts for emoji support
        Font emojiFont = null;
        String[] fontNames = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Segoe UI Symbol", "Dialog"};
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.PLAIN, 36);
            if (testFont.getFamily().equals(fontName) || testFont.canDisplayUpTo(emoji) == -1) {
                emojiFont = testFont;
                break;
            }
        }
        if (emojiFont == null) {
            emojiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 36);
        }
        emojiLabel.setFont(emojiFont);
        emojiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(emojiLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createAnnouncementsPreview() {
        JPanel panel = createModernCard();
        panel.setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("📢 Recent Announcements");
        titleLabel.setFont(getEmojiFont(18).deriveFont(Font.BOLD));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CARD_BG);
        
        List<Announcement> announcements = announcementController.listAll();
        for (Announcement ann : announcements) {
            listPanel.add(createAnnouncementItem(ann));
            listPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEventsPreview() {
        JPanel panel = createModernCard();
        panel.setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("📅 Upcoming Events");
        titleLabel.setFont(getEmojiFont(18).deriveFont(Font.BOLD));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CARD_BG);
        
        List<com.fstgc.vms.model.Event> events = eventController.listAll();
        for (com.fstgc.vms.model.Event event : events.stream().limit(3).toList()) {
            listPanel.add(createEventItem(event));
            listPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAnnouncementItem(Announcement ann) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(CARD_BG);
        
        // Priority-based border color
        Color borderColor;
        switch (ann.getPriority()) {
            case URGENT:
                borderColor = new Color(220, 38, 38); // Red
                break;
            case HIGH:
                borderColor = ORANGE; // Orange
                break;
            case MEDIUM:
                borderColor = PRIMARY_BLUE; // Blue
                break;
            case LOW:
                borderColor = new Color(107, 114, 128); // Gray
                break;
            default:
                borderColor = PRIMARY_BLUE;
        }
        
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, borderColor),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(ann.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel priorityBadge = new JLabel(ann.getPriority().toString());
        priorityBadge.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        priorityBadge.setForeground(Color.WHITE);
        priorityBadge.setOpaque(true);
        priorityBadge.setBackground(borderColor);
        priorityBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        
        headerPanel.add(titleLabel);
        headerPanel.add(priorityBadge);
        
        JLabel messageLabel = new JLabel("<html>" + ann.getMessage() + "</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(TEXT_SECONDARY);
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(messageLabel);
        
        item.add(contentPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    private JPanel createEventItem(com.fstgc.vms.model.Event event) {
        JPanel item = createModernCard();
        item.setLayout(new BorderLayout(10, 10));
        item.setBorder(BorderFactory.createCompoundBorder(
            item.getBorder(),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Create date label with emoji support
        JLabel dateLabel = new JLabel("📅 " + event.getEventDate() + " | 📍 " + event.getLocation());
        // Set emoji-supporting font
        Font emojiFont = null;
        String[] fontNames = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Segoe UI Symbol", "Dialog"};
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.PLAIN, 12);
            if (testFont.getFamily().equals(fontName) || testFont.canDisplayUpTo("📅📍") == -1) {
                emojiFont = testFont;
                break;
            }
        }
        if (emojiFont == null) {
            emojiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
        dateLabel.setFont(emojiFont);
        dateLabel.setForeground(TEXT_SECONDARY);
        
        // Add registration counter
        int totalCapacity = event.getCapacity() + event.getCurrentRegistrations();
        JLabel registeredLabel = new JLabel("Registered: " + event.getCurrentRegistrations() + " | Capacity: " + totalCapacity);
        registeredLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        registeredLabel.setForeground(TEXT_SECONDARY);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(dateLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(registeredLabel);
        
        item.add(contentPanel, BorderLayout.CENTER);
        
        JButton registerBtn = createModernButton("Register", PRIMARY_BLUE);
        registerBtn.setPreferredSize(new Dimension(100, 30));
        item.add(registerBtn, BorderLayout.SOUTH);
        
        return item;
    }
    
    private JPanel createModernCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }
    
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        return table;
    }

    private JPanel createVolunteerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with title and (for admins) add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GRAY_BG);
        
        JLabel titleLabel = new JLabel("Volunteers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // If current user is a volunteer, they should not see the directory at all
        Role role = authService.getCurrentUserRole();
        if (role == Role.VOLUNTEER) {
            panel.add(headerPanel, BorderLayout.NORTH);

            JPanel restrictedCard = createModernCard();
            restrictedCard.setLayout(new BorderLayout());
            JLabel msg = new JLabel("Volunteer directory is only visible to admins and coordinators.");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            msg.setForeground(TEXT_SECONDARY);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            restrictedCard.add(msg, BorderLayout.CENTER);

            panel.add(restrictedCard, BorderLayout.CENTER);
            return panel;
        }

        JButton addBtn = createModernButton("+ Add Volunteer", PRIMARY_BLUE);
        addBtn.addActionListener(e -> showAddVolunteerDialog());
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout());
        
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Total Hours", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        // Populate table
        List<Volunteer> volunteers = volunteerController.listAll();
        for (Volunteer v : volunteers) {
            String auditInfo = v.getLastModifiedBy() != null ? 
                v.getLastModifiedBy() + " (" + (v.getLastModifiedDate() != null ? 
                v.getLastModifiedDate().toLocalDate().toString() : "N/A") + ")" : "N/A";
            tableModel.addRow(new Object[]{
                v.getId(),
                v.getFirstName() + " " + v.getLastName(),
                v.getEmail(),
                v.getPhone(),
                String.format("%.1f hrs", v.getTotalHoursWorked()),
                v.getStatus(),
                auditInfo
            });
        }

        // Action buttons for admins
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionsPanel.setBackground(CARD_BG);

        JButton editBtn = createModernButton("Edit", PRIMARY_BLUE);
        JButton deleteBtn = createModernButton("Delete", new Color(220, 38, 38));
        JButton statusBtn = createModernButton("Change Status", PURPLE);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a volunteer to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            showEditVolunteerDialog(id);
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a volunteer to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete volunteer #" + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    volunteerController.delete(id);
                    JOptionPane.showMessageDialog(this, "Volunteer deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    refreshVolunteerPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        statusBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a volunteer to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            showChangeVolunteerStatusDialog(id);
        });

        actionsPanel.add(statusBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);

        
        // Add mouse listener for row actions
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int volunteerId = (int) tableModel.getValueAt(row, 0);
                        showEditVolunteerDialog(volunteerId);
                    }
                }
            }
        });
        
        // Add action buttons panel
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(actionsPanel, BorderLayout.SOUTH);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }
    
    private void showAddVolunteerDialog() {
        JDialog dialog = new JDialog(this, "Register New Volunteer", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField firstNameField = createModernTextField();
        JTextField lastNameField = createModernTextField();
        JTextField emailField = createModernTextField();
        JTextField phoneField = createModernTextField();
        JComboBox<VolunteerStatus> statusCombo = new JComboBox<>(VolunteerStatus.values());
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(createLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(createLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(createLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createLabel("Status:"));
        formPanel.add(statusCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Register", PRIMARY_BLUE);
        saveBtn.addActionListener(e -> {
            try {
                Volunteer v = new Volunteer();
                v.setFirstName(firstNameField.getText());
                v.setLastName(lastNameField.getText());
                v.setEmail(emailField.getText());
                v.setPhone(phoneField.getText());
                v.setStatus((VolunteerStatus) statusCombo.getSelectedItem());
                v.setLastModifiedBy(authService.getCurrentUser().getUsername());
                v.setLastModifiedDate(java.time.LocalDateTime.now());
                v = volunteerController.register(v);
                JOptionPane.showMessageDialog(dialog, 
                    "Volunteer registered successfully!\nID: " + v.getId() + "\nHours: " + v.getTotalHoursWorked(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void deleteVolunteer(int volunteerId) {
        try {
            volunteerController.delete(volunteerId);
            JOptionPane.showMessageDialog(this, 
                "Volunteer deleted successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllPanels();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshVolunteerPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(1, createVolunteerPanel());
        refreshDashboard();
        tabbedPane.setSelectedIndex(selectedIndex);
    }

    private void showEditVolunteerDialog(int volunteerId) {
        volunteerController.get(volunteerId).ifPresentOrElse(existing -> {
            JDialog dialog = new JDialog(this, "Edit Volunteer", true);
            dialog.setSize(450, 350);
            dialog.setLocationRelativeTo(this);

            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBackground(CARD_BG);
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
            formPanel.setBackground(CARD_BG);

            JTextField firstNameField = createModernTextField();
            firstNameField.setText(existing.getFirstName());
            JTextField lastNameField = createModernTextField();
            lastNameField.setText(existing.getLastName());
            JTextField emailField = createModernTextField();
            emailField.setText(existing.getEmail());
            JTextField phoneField = createModernTextField();
            phoneField.setText(existing.getPhone());
            JComboBox<VolunteerStatus> statusCombo = new JComboBox<>(VolunteerStatus.values());
            statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            statusCombo.setSelectedItem(existing.getStatus());

            formPanel.add(createLabel("First Name:"));
            formPanel.add(firstNameField);
            formPanel.add(createLabel("Last Name:"));
            formPanel.add(lastNameField);
            formPanel.add(createLabel("Email:"));
            formPanel.add(emailField);
            formPanel.add(createLabel("Phone:"));
            formPanel.add(phoneField);
            formPanel.add(createLabel("Status:"));
            formPanel.add(statusCombo);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(CARD_BG);

            JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
            cancelBtn.addActionListener(e -> dialog.dispose());

            JButton saveBtn = createModernButton("Save", PRIMARY_BLUE);
            saveBtn.addActionListener(e -> {
                try {
                    VolunteerStatus status = (VolunteerStatus) statusCombo.getSelectedItem();
                    volunteerController.updateVolunteer(
                        volunteerId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        status
                    );
                    JOptionPane.showMessageDialog(dialog,
                        "Volunteer updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshVolunteerPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            buttonPanel.add(cancelBtn);
            buttonPanel.add(saveBtn);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);
        }, () -> JOptionPane.showMessageDialog(this,
                "Volunteer not found.",
                "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void showChangeVolunteerStatusDialog(int volunteerId) {
        volunteerController.get(volunteerId).ifPresentOrElse(existing -> {
            JDialog dialog = new JDialog(this, "Change Volunteer Status", true);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(this);

            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBackground(CARD_BG);
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 15));
            formPanel.setBackground(CARD_BG);

            JComboBox<VolunteerStatus> statusCombo = new JComboBox<>(VolunteerStatus.values());
            statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            statusCombo.setSelectedItem(existing.getStatus());

            formPanel.add(createLabel("New Status:"));
            formPanel.add(statusCombo);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(CARD_BG);

            JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
            cancelBtn.addActionListener(e -> dialog.dispose());

            JButton saveBtn = createModernButton("Update", PURPLE);
            saveBtn.addActionListener(e -> {
                try {
                    VolunteerStatus status = (VolunteerStatus) statusCombo.getSelectedItem();
                    volunteerController.changeStatus(volunteerId, status);
                    JOptionPane.showMessageDialog(dialog,
                        "Volunteer status updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshVolunteerPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            buttonPanel.add(cancelBtn);
            buttonPanel.add(saveBtn);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);
        }, () -> JOptionPane.showMessageDialog(this,
                "Volunteer not found.",
                "Error", JOptionPane.ERROR_MESSAGE));
    }
    
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private Font getEmojiFont(int size) {
        // Try multiple fonts for emoji support with fallback
        String[] fontNames = {"Segoe UI Emoji", "Segoe UI Symbol", "Apple Color Emoji", "Noto Color Emoji", "Arial Unicode MS", "Symbola"};
        for (String fontName : fontNames) {
            Font testFont = new Font(fontName, Font.PLAIN, size);
            // Check if font exists or can display emoji characters
            if (testFont.getFamily().equalsIgnoreCase(fontName) || 
                testFont.canDisplayUpTo("🏆🥇") == -1) {
                return testFont;
            }
        }
        // Final fallback to Dialog which often works on Windows
        return new Font("Dialog", Font.PLAIN, size);
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GRAY_BG);
        
        JLabel titleLabel = new JLabel("Events");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton addBtn = createModernButton("+ Create Event", GREEN);
        addBtn.addActionListener(e -> showAddEventDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Only admins/coordinators can create events; hide the button for volunteers
        Role role = authService.getCurrentUserRole();
        if (role != Role.VOLUNTEER) {
            headerPanel.add(addBtn, BorderLayout.EAST);
        }
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Events grid
        JPanel eventsGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        eventsGrid.setBackground(GRAY_BG);
        
        List<com.fstgc.vms.model.Event> events = eventController.listAll();
        for (com.fstgc.vms.model.Event event : events) {
            eventsGrid.add(createEventCard(event));
        }
        
        JScrollPane scrollPane = new JScrollPane(eventsGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(GRAY_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createEventCard(com.fstgc.vms.model.Event event) {
        JPanel card = createModernCard();
        card.setLayout(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(300, 200));
        
        // Color bar at top
        JPanel colorBar = new JPanel();
        colorBar.setPreferredSize(new Dimension(0, 4));
        colorBar.setBackground(event.getEventType() == EventType.COMMUNITY_SERVICE ? GREEN : PRIMARY_BLUE);
        card.add(colorBar, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(event.getEventType().toString().replace("_", " "));
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setOpaque(true);
        typeLabel.setBackground(PRIMARY_BLUE);
        typeLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Use emoji-supporting font for labels with emojis
        Font emojiFont = getEmojiFont(12);
        
        JLabel dateLabel = new JLabel("📅 " + event.getEventDate());
        dateLabel.setFont(emojiFont);
        dateLabel.setForeground(TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel locationLabel = new JLabel("📍 " + (event.getLocation() != null ? event.getLocation() : "TBD"));
        locationLabel.setFont(emojiFont);
        locationLabel.setForeground(TEXT_SECONDARY);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Show registered count and total capacity
        int totalCapacity = event.getCapacity() + event.getCurrentRegistrations();
        JLabel capacityLabel = new JLabel("👥 Registered: " + event.getCurrentRegistrations() + " | Capacity: " + totalCapacity);
        capacityLabel.setFont(emojiFont);
        capacityLabel.setForeground(TEXT_SECONDARY);
        capacityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(typeLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(dateLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(locationLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(capacityLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(CARD_BG);
        
        Role userRole = authService.getCurrentUserRole();
        
        // For admins: show Edit and Delete buttons
        if (userRole != Role.VOLUNTEER) {
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setForeground(PRIMARY_BLUE);
            editBtn.setBackground(Color.WHITE);
            editBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.addActionListener(e -> showEditEventDialog(event.getEventId()));
            
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteBtn.setForeground(new Color(239, 68, 68));
            deleteBtn.setBackground(Color.WHITE);
            deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(239, 68, 68), 1));
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete event: " + event.getTitle() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteEvent(event.getEventId());
                }
            });
            
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
        } else {
            // For volunteers: show Register button
            // Check if volunteer is already registered
            SystemAdmin currentUser = authService.getCurrentUser();
            boolean isRegistered = isVolunteerRegisteredForEvent(currentUser.getId(), event.getEventId());
            
            JButton registerBtn = new JButton(isRegistered ? "✓ Registered" : "Register");
            registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            registerBtn.setForeground(isRegistered ? new Color(34, 197, 94) : PRIMARY_BLUE);
            registerBtn.setBackground(Color.WHITE);
            registerBtn.setBorder(BorderFactory.createLineBorder(
                isRegistered ? new Color(34, 197, 94) : PRIMARY_BLUE, 1));
            registerBtn.setFocusPainted(false);
            registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            registerBtn.setEnabled(!isRegistered);
            
            if (!isRegistered) {
                registerBtn.addActionListener(e -> registerVolunteerForEvent(event));
            }
            
            buttonPanel.add(registerBtn);
        }
        
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void showAddEventDialog() {
        JDialog dialog = new JDialog(this, "Create New Event", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField titleField = createModernTextField();
        JTextField dateField = createModernTextField();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        dateField.setText(LocalDate.now().format(formatter));
        JTextField locationField = createModernTextField();
        JTextField capacityField = createModernTextField();
        JComboBox<EventType> typeCombo = new JComboBox<>(EventType.values());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(createLabel("Date (MM-DD-YYYY):"));
        formPanel.add(dateField);
        formPanel.add(createLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(createLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(createLabel("Type:"));
        formPanel.add(typeCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Create", GREEN);
        saveBtn.addActionListener(e -> {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                com.fstgc.vms.model.Event event = eventController.create(
                    titleField.getText(),
                    LocalDate.parse(dateField.getText(), inputFormatter),
                    Integer.parseInt(capacityField.getText()),
                    (EventType) typeCombo.getSelectedItem(),
                    locationField.getText()
                );
                JOptionPane.showMessageDialog(dialog, 
                    "Event created successfully!\nID: " + event.getEventId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showEditEventDialog(int eventId) {
        Event event = eventController.get(eventId);
        if (event == null) {
            JOptionPane.showMessageDialog(this, "Event not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Event", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField titleField = createModernTextField();
        titleField.setText(event.getTitle());
        
        JTextField dateField = createModernTextField();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        dateField.setText(event.getEventDate().format(formatter));
        
        JTextField locationField = createModernTextField();
        locationField.setText(event.getLocation());
        
        JTextField capacityField = createModernTextField();
        capacityField.setText(String.valueOf(event.getCapacity()));
        
        JComboBox<EventType> typeCombo = new JComboBox<>(EventType.values());
        typeCombo.setSelectedItem(event.getEventType());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(createLabel("Date (MM-DD-YYYY):"));
        formPanel.add(dateField);
        formPanel.add(createLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(createLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(createLabel("Type:"));
        formPanel.add(typeCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Save Changes", PRIMARY_BLUE);
        saveBtn.addActionListener(e -> {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                event.setTitle(titleField.getText());
                event.setEventDate(LocalDate.parse(dateField.getText(), inputFormatter));
                event.setLocation(locationField.getText());
                event.setCapacity(Integer.parseInt(capacityField.getText()));
                event.setEventType((EventType) typeCombo.getSelectedItem());
                event.setLastModifiedBy(authService.getCurrentUser().getUsername());
                event.setLastModifiedDate(LocalDateTime.now());
                
                eventController.update(event);
                JOptionPane.showMessageDialog(dialog, 
                    "Event updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void deleteEvent(int eventId) {
        if (eventController.delete(eventId)) {
            JOptionPane.showMessageDialog(this, "Event deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete event!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isVolunteerRegisteredForEvent(int volunteerId, int eventId) {
        // Check if volunteer has already registered for this event
        // by checking if an attendance record exists
        return attendanceController.isVolunteerRegisteredForEvent(volunteerId, eventId);
    }
    
    private void registerVolunteerForEvent(com.fstgc.vms.model.Event event) {
        SystemAdmin currentUser = authService.getCurrentUser();
        
        try {
            // Check if volunteer is already registered
            if (isVolunteerRegisteredForEvent(currentUser.getId(), event.getEventId())) {
                JOptionPane.showMessageDialog(this, 
                    "You are already registered for this event!", 
                    "Already Registered", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if event is at capacity
            if (event.getCurrentRegistrations() >= event.getCapacity()) {
                JOptionPane.showMessageDialog(this, 
                    "This event is at full capacity!", 
                    "Event Full", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Register volunteer for event (check-in immediately)
            Attendance attendance = attendanceController.checkIn(currentUser.getId(), event.getEventId());
            
            JOptionPane.showMessageDialog(this, 
                "Successfully registered for event!\nAttendance ID: " + attendance.getAttendanceId(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            refreshAllPanels();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error registering for event: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshEventPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(2, createEventPanel());
        refreshDashboard();
        tabbedPane.setSelectedIndex(selectedIndex);
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GRAY_BG);
        
        JLabel titleLabel = new JLabel("Attendance Records");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton addBtn = createModernButton("+ Record Attendance", PURPLE);
        addBtn.addActionListener(e -> showAttendanceDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout());
        
        String[] columnNames = {"ID", "Volunteer ID", "Event ID", "Check In", "Check Out", "Hours", "Status", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createModernTable(tableModel);
        
        List<Attendance> attendances = attendanceController.listAll();
        for (Attendance a : attendances) {
            tableModel.addRow(new Object[]{
                a.getAttendanceId(),
                a.getVolunteerId(),
                a.getEventId(),
                a.getCheckInTime(),
                a.getCheckOutTime(),
                String.format("%.1f hrs", a.getHoursWorked()),
                a.getStatus(),
                "Update Status"
            });
        }
        
        // Add button column renderer and editor
        table.getColumn("Action").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JButton btn = createModernButton("Update Status", PURPLE);
            btn.setPreferredSize(new Dimension(120, 30));
            return btn;
        });
        
        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
                JButton btn = createModernButton("Update Status", PURPLE);
                btn.addActionListener(e -> {
                    int attendanceId = (int) tbl.getValueAt(row, 0);
                    SystemUI.this.showUpdateAttendanceStatusDialog(attendanceId);
                });
                return btn;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }
    
    private void showAttendanceDialog() {
        JDialog dialog = new JDialog(this, "Record Attendance", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField volunteerIdField = createModernTextField();
        JTextField eventIdField = createModernTextField();
        JTextField attendanceIdField = createModernTextField();
        
        formPanel.add(createLabel("Volunteer ID:"));
        formPanel.add(volunteerIdField);
        formPanel.add(createLabel("Event ID:"));
        formPanel.add(eventIdField);
        formPanel.add(createLabel("Attendance ID (Check-out):"));
        formPanel.add(attendanceIdField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton checkInBtn = createModernButton("Check In", GREEN);
        checkInBtn.addActionListener(e -> {
            try {
                Attendance a = attendanceController.checkIn(
                    Integer.parseInt(volunteerIdField.getText()),
                    Integer.parseInt(eventIdField.getText())
                );
                JOptionPane.showMessageDialog(dialog, "Checked in! Attendance ID: " + a.getAttendanceId());
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton checkOutBtn = createModernButton("Check Out", ORANGE);
        checkOutBtn.addActionListener(e -> {
            try {
                Attendance a = attendanceController.checkOut(Integer.parseInt(attendanceIdField.getText()));
                JOptionPane.showMessageDialog(dialog, "Checked out! Hours: " + a.getHoursWorked());
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(checkInBtn);
        buttonPanel.add(checkOutBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void refreshAttendancePanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(3, createAttendancePanel());
        refreshDashboard();
        tabbedPane.setSelectedIndex(selectedIndex);
    }

    private JPanel createTimesheetPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GRAY_BG);
        
        JLabel titleLabel = new JLabel("Timesheets");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton createBtn = createModernButton("+ Create Timesheet", GREEN);
        createBtn.addActionListener(e -> showCreateTimesheetDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(createBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Cards grid
        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        cardsPanel.setBackground(GRAY_BG);
        
        List<Volunteer> volunteers = volunteerController.listAll();
        for (Volunteer vol : volunteers) {
            cardsPanel.add(createTimesheetCard(vol));
        }
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(GRAY_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTimesheetCard(Volunteer vol) {
        JPanel card = createModernCard();
        card.setLayout(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(280, 220));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        // Avatar
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        avatarPanel.setBackground(CARD_BG);
        JLabel avatar = new JLabel(vol.getFirstName().substring(0,1) + vol.getLastName().substring(0,1));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(PRIMARY_BLUE);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        avatarPanel.add(avatar);
        
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(CARD_BG);
        JLabel nameLabel = new JLabel(vol.getFirstName() + " " + vol.getLastName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        JLabel periodLabel = new JLabel("November 2025");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        periodLabel.setForeground(TEXT_SECONDARY);
        namePanel.add(nameLabel);
        namePanel.add(periodLabel);
        avatarPanel.add(namePanel);
        
        contentPanel.add(avatarPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 8));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        statsPanel.add(createLabel("Total Hours:"));
        JLabel hoursLabel = new JLabel(String.format("%.1f hrs", vol.getTotalHoursWorked()));
        hoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsPanel.add(hoursLabel);
        
        statsPanel.add(createLabel("Events Attended:"));
        JLabel eventsLabel = new JLabel(String.valueOf(vol.getEventsAttended()));
        eventsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsPanel.add(eventsLabel);
        
        statsPanel.add(createLabel("Status:"));
        JLabel statusLabel = new JLabel("Approved");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(GREEN);
        statusLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        statsPanel.add(statusLabel);
        
        contentPanel.add(statsPanel);
        card.add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(CARD_BG);
        JButton submitBtn = createModernButton("Submit Timesheet", GREEN);
        submitBtn.addActionListener(e -> showSubmitTimesheetDialog(vol.getId()));
        buttonPanel.add(submitBtn);
        
        // Add Edit button for admin
        if (authService.getCurrentUser().getRole() == Role.SUPER_ADMIN || 
            authService.getCurrentUser().getRole() == Role.ADMIN) {
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setForeground(PRIMARY_BLUE);
            editBtn.setBackground(Color.WHITE);
            editBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.addActionListener(e -> showEditTimesheetDialog(vol.getId()));
            buttonPanel.add(editBtn);
        }
        
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createAnnouncementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GRAY_BG);
        
        JLabel titleLabel = new JLabel("Announcements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton addBtn = createModernButton("+ New Announcement", ORANGE);
        addBtn.addActionListener(e -> showAddAnnouncementDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Announcements list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(GRAY_BG);
        
        List<Announcement> announcements = announcementController.listAll();
        for (Announcement ann : announcements) {
            listPanel.add(createFullAnnouncementCard(ann));
            listPanel.add(Box.createVerticalStrut(15));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(GRAY_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFullAnnouncementCard(Announcement ann) {
        JPanel card = createModernCard();
        card.setLayout(new BorderLayout(15, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setBackground(CARD_BG);
        
        JLabel icon = new JLabel("\uD83D\uDCE2"); // 📢 megaphone
        Font iconFont = getEmojiFont(24);
        icon.setFont(iconFont);
        
        JLabel title = new JLabel(ann.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        
        leftHeader.add(icon);
        leftHeader.add(title);
        
        JLabel priorityLabel = new JLabel(ann.getPriority().toString());
        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        priorityLabel.setForeground(Color.WHITE);
        priorityLabel.setOpaque(true);
        // Color based on priority level
        Color priorityColor;
        switch (ann.getPriority()) {
            case URGENT:
                priorityColor = new Color(220, 38, 38); // Red
                break;
            case HIGH:
                priorityColor = ORANGE; // Orange
                break;
            case MEDIUM:
                priorityColor = PRIMARY_BLUE; // Blue
                break;
            case LOW:
                priorityColor = new Color(107, 114, 128); // Gray
                break;
            default:
                priorityColor = PRIMARY_BLUE;
        }
        priorityLabel.setBackground(priorityColor);
        priorityLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(priorityLabel, BorderLayout.EAST);
        
        JLabel messageLabel = new JLabel("<html>" + ann.getMessage() + "</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_PRIMARY);
        
        JLabel dateLabel = new JLabel("Posted on " + ann.getPublishedDate());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(dateLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton editBtn = new JButton("Edit");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editBtn.setForeground(PRIMARY_BLUE);
        editBtn.setBackground(Color.WHITE);
        editBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> showEditAnnouncementDialog(ann.getAnnouncementId()));
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteBtn.setForeground(new Color(239, 68, 68));
        deleteBtn.setBackground(Color.WHITE);
        deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(239, 68, 68), 1));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete announcement: " + ann.getTitle() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteAnnouncement(ann.getAnnouncementId());
            }
        });
        
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void showAddAnnouncementDialog() {
        JDialog dialog = new JDialog(this, "New Announcement", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(CARD_BG);
        
        JTextField titleField = createModernTextField();
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setBorder(messageArea.getBorder());
        
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(CARD_BG);
        titlePanel.add(createLabel("Title:"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBackground(CARD_BG);
        messagePanel.add(createLabel("Message:"), BorderLayout.NORTH);
        messagePanel.add(messageScroll, BorderLayout.CENTER);
        
        JPanel priorityPanel = new JPanel(new BorderLayout(5, 5));
        priorityPanel.setBackground(CARD_BG);
        priorityPanel.add(createLabel("Priority:"), BorderLayout.NORTH);
        priorityPanel.add(priorityCombo, BorderLayout.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CARD_BG);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(priorityPanel, BorderLayout.CENTER);
        
        formPanel.add(topPanel, BorderLayout.NORTH);
        formPanel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton publishBtn = createModernButton("Publish", ORANGE);
        publishBtn.addActionListener(e -> {
            try {
                Announcement a = announcementController.publish(
                    titleField.getText(),
                    messageArea.getText(),
                    (Priority) priorityCombo.getSelectedItem()
                );
                JOptionPane.showMessageDialog(dialog, 
                    "Announcement published!\nID: " + a.getAnnouncementId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(publishBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showEditAnnouncementDialog(int announcementId) {
        Announcement announcement = announcementController.get(announcementId);
        if (announcement == null) {
            JOptionPane.showMessageDialog(this, "Announcement not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Announcement", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(CARD_BG);
        
        JTextField titleField = createModernTextField();
        titleField.setText(announcement.getTitle());
        
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setText(announcement.getMessage());
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setBorder(messageArea.getBorder());
        
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setSelectedItem(announcement.getPriority());
        
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(CARD_BG);
        titlePanel.add(createLabel("Title:"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBackground(CARD_BG);
        messagePanel.add(createLabel("Message:"), BorderLayout.NORTH);
        messagePanel.add(messageScroll, BorderLayout.CENTER);
        
        JPanel priorityPanel = new JPanel(new BorderLayout(5, 5));
        priorityPanel.setBackground(CARD_BG);
        priorityPanel.add(createLabel("Priority:"), BorderLayout.NORTH);
        priorityPanel.add(priorityCombo, BorderLayout.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CARD_BG);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(priorityPanel, BorderLayout.CENTER);
        
        formPanel.add(topPanel, BorderLayout.NORTH);
        formPanel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Save Changes", PRIMARY_BLUE);
        saveBtn.addActionListener(e -> {
            try {
                announcement.setTitle(titleField.getText());
                announcement.setMessage(messageArea.getText());
                announcement.setPriority((Priority) priorityCombo.getSelectedItem());
                announcement.setLastModifiedBy(authService.getCurrentUser().getUsername());
                announcement.setLastModifiedDate(LocalDateTime.now());
                
                announcementController.update(announcement);
                JOptionPane.showMessageDialog(dialog, 
                    "Announcement updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void deleteAnnouncement(int announcementId) {
        if (announcementController.delete(announcementId)) {
            JOptionPane.showMessageDialog(this, "Announcement deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete announcement!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshAnnouncementPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(6, createAnnouncementPanel());
        refreshDashboard();
        tabbedPane.setSelectedIndex(selectedIndex);
    }

    private JPanel createAwardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GRAY_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Awards & Badges");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(GRAY_BG);
        
        // Badge tiers
        JPanel badgesCard = createModernCard();
        badgesCard.setLayout(new BorderLayout(10, 10));
        badgesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel badgeTitle = new JLabel("Achievement Tiers");
        badgeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        badgeTitle.setForeground(TEXT_PRIMARY);
        badgesCard.add(badgeTitle, BorderLayout.NORTH);
        
        JPanel badgesGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        badgesGrid.setBackground(CARD_BG);
        
        // At startup, all badge tiers show 0 earned
        badgesGrid.add(createBadgeCard("🥉 Bronze", "10+ hours", new Color(205, 127, 50), 0));
        badgesGrid.add(createBadgeCard("🥈 Silver", "50+ hours", new Color(192, 192, 192), 0));
        badgesGrid.add(createBadgeCard("🥇 Gold", "100+ hours", new Color(255, 215, 0), 0));
        badgesGrid.add(createBadgeCard("💎 Platinum", "200+ hours", PRIMARY_BLUE, 0));
        
        badgesCard.add(badgesGrid, BorderLayout.CENTER);
        contentPanel.add(badgesCard);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Leaderboard
        JPanel leaderboardCard = createModernCard();
        leaderboardCard.setLayout(new BorderLayout(10, 10));
        
        JLabel leaderTitle = new JLabel("Leaderboard");
        leaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leaderTitle.setForeground(TEXT_PRIMARY);
        leaderboardCard.add(leaderTitle, BorderLayout.NORTH);
        
        JPanel leaderList = new JPanel();
        leaderList.setLayout(new BoxLayout(leaderList, BoxLayout.Y_AXIS));
        leaderList.setBackground(CARD_BG);
        
        List<Volunteer> volunteers = volunteerController.listAll();
        volunteers.sort((a, b) -> Integer.compare(b.getBadgesEarned(), a.getBadgesEarned()));
        
        Color[] medalColors = {new Color(255, 215, 0), new Color(192, 192, 192), new Color(205, 127, 50)};
        int rank = 1;
        for (Volunteer vol : volunteers) {
            leaderList.add(createLeaderboardItem(vol, rank, rank <= 3 ? medalColors[rank-1] : new Color(229, 231, 235)));
            leaderList.add(Box.createVerticalStrut(8));
            rank++;
        }
        
        JScrollPane leaderScroll = new JScrollPane(leaderList);
        leaderScroll.setBorder(null);
        leaderScroll.setBackground(CARD_BG);
        leaderboardCard.add(leaderScroll, BorderLayout.CENTER);
        
        contentPanel.add(leaderboardCard);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(GRAY_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBadgeCard(String name, String requirement, Color color, int count) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(getEmojiFont(16).deriveFont(Font.BOLD));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel reqLabel = new JLabel(requirement);
        reqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reqLabel.setForeground(new Color(255, 255, 255, 230));
        reqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel countLabel = new JLabel(count + " earned");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        countLabel.setForeground(Color.WHITE);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(reqLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(countLabel);
        
        return card;
    }
    
    private JPanel createLeaderboardItem(Volunteer vol, int rank, Color rankColor) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(CARD_BG);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel rankLabel = new JLabel(String.valueOf(rank));
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rankLabel.setForeground(rank <= 3 ? Color.WHITE : TEXT_PRIMARY);
        rankLabel.setOpaque(true);
        rankLabel.setBackground(rankColor);
        rankLabel.setPreferredSize(new Dimension(40, 40));
        rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);
        
        JLabel nameLabel = new JLabel(vol.getFirstName() + " " + vol.getLastName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        
        JLabel badgesLabel = new JLabel(vol.getBadgesEarned() + " badge" + (vol.getBadgesEarned() != 1 ? "s" : "") + " earned");
        badgesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        badgesLabel.setForeground(TEXT_SECONDARY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(badgesLabel);
        
        JLabel trophy = new JLabel("\uD83C\uDFC6"); // 🏆 trophy
        trophy.setFont(getEmojiFont(28));
        
        item.add(rankLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(trophy, BorderLayout.EAST);
        
        return item;
    }

    private void showUpdateAttendanceStatusDialog(int attendanceId) {
        JDialog dialog = new JDialog(this, "Update Attendance Status", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JComboBox<AttendanceStatus> statusCombo = new JComboBox<>(AttendanceStatus.values());
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("New Status:"));
        formPanel.add(statusCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Update", PURPLE);
        saveBtn.addActionListener(e -> {
            try {
                attendanceController.updateStatus(attendanceId, (AttendanceStatus) statusCombo.getSelectedItem());
                JOptionPane.showMessageDialog(dialog, 
                    "Attendance status updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showSubmitTimesheetDialog(int volunteerId) {
        JDialog dialog = new JDialog(this, "Submit Timesheet", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField startDateField = createModernTextField();
        startDateField.setText(LocalDate.now().withDayOfMonth(1).toString());
        JTextField endDateField = createModernTextField();
        endDateField.setText(LocalDate.now().toString());
        JComboBox<TimesheetStatus> statusCombo = new JComboBox<>(TimesheetStatus.values());
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("Start Date (MM-DD-YYYY):"));
        formPanel.add(startDateField);
        formPanel.add(createLabel("End Date (MM-DD-YYYY):"));
        formPanel.add(endDateField);
        formPanel.add(createLabel("Status:"));
        formPanel.add(statusCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton submitBtn = createModernButton("Submit", GREEN);
        submitBtn.addActionListener(e -> {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                Timesheet ts = timesheetController.submit(
                    volunteerId,
                    LocalDate.parse(startDateField.getText(), inputFormatter),
                    LocalDate.parse(endDateField.getText(), inputFormatter),
                    (TimesheetStatus) statusCombo.getSelectedItem()
                );
                JOptionPane.showMessageDialog(dialog, 
                    "Timesheet submitted successfully!\\nID: " + ts.getTimesheetId() + "\\nTotal Hours: " + ts.getTotalHours(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showEditTimesheetDialog(int volunteerId) {
        // Get existing timesheets for this volunteer
        List<Timesheet> timesheets = timesheetController.listAll().stream()
            .filter(ts -> ts.getVolunteerId() == volunteerId)
            .toList();
        
        if (timesheets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No timesheets found for this volunteer!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Let admin select which timesheet to edit
        String[] options = timesheets.stream()
            .map(ts -> "ID: " + ts.getTimesheetId() + " | " + ts.getPeriodStartDate() + " to " + ts.getPeriodEndDate() + " | " + ts.getTotalHours() + " hrs")
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select timesheet to edit:",
            "Edit Timesheet",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selected == null) return;
        
        int timesheetId = Integer.parseInt(selected.split(" \\| ")[0].replace("ID: ", ""));
        Timesheet timesheet = timesheets.stream()
            .filter(ts -> ts.getTimesheetId() == timesheetId)
            .findFirst()
            .orElse(null);
        
        if (timesheet == null) return;
        
        JDialog dialog = new JDialog(this, "Edit Timesheet", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        JTextField idField = createModernTextField();
        idField.setText(String.valueOf(timesheet.getTimesheetId()));
        idField.setEditable(false);
        
        JTextField startDateField = createModernTextField();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        startDateField.setText(timesheet.getPeriodStartDate().format(formatter));
        
        JTextField endDateField = createModernTextField();
        endDateField.setText(timesheet.getPeriodEndDate().format(formatter));
        
        JTextField totalHoursField = createModernTextField();
        totalHoursField.setText(String.valueOf(timesheet.getTotalHours()));
        
        JComboBox<TimesheetStatus> statusCombo = new JComboBox<>(TimesheetStatus.values());
        statusCombo.setSelectedItem(timesheet.getApprovalStatus());
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("Timesheet ID:"));
        formPanel.add(idField);
        formPanel.add(createLabel("Start Date (MM-DD-YYYY):"));
        formPanel.add(startDateField);
        formPanel.add(createLabel("End Date (MM-DD-YYYY):"));
        formPanel.add(endDateField);
        formPanel.add(createLabel("Total Hours:"));
        formPanel.add(totalHoursField);
        formPanel.add(createLabel("Status:"));
        formPanel.add(statusCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = createModernButton("Save Changes", PRIMARY_BLUE);
        saveBtn.addActionListener(e -> {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                timesheet.setPeriodStartDate(LocalDate.parse(startDateField.getText(), inputFormatter));
                timesheet.setPeriodEndDate(LocalDate.parse(endDateField.getText(), inputFormatter));
                timesheet.setTotalHours(Double.parseDouble(totalHoursField.getText()));
                timesheet.setApprovalStatus((TimesheetStatus) statusCombo.getSelectedItem());
                timesheet.setLastModifiedBy(authService.getCurrentUser().getUsername());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                
                timesheetController.update(timesheet);
                JOptionPane.showMessageDialog(dialog, 
                    "Timesheet updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showCreateTimesheetDialog() {
        JDialog dialog = new JDialog(this, "Create Timesheet for Volunteer", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBackground(CARD_BG);
        
        // Create volunteer dropdown
        List<Volunteer> volunteers = volunteerController.listAll();
        JComboBox<String> volunteerCombo = new JComboBox<>();
        volunteerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (Volunteer v : volunteers) {
            volunteerCombo.addItem(v.getId() + " - " + v.getFirstName() + " " + v.getLastName());
        }
        
        JTextField startDateField = createModernTextField();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        startDateField.setText(LocalDate.now().withDayOfMonth(1).format(formatter));
        JTextField endDateField = createModernTextField();
        endDateField.setText(LocalDate.now().format(formatter));
        JComboBox<TimesheetStatus> statusCombo = new JComboBox<>(TimesheetStatus.values());
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(createLabel("Volunteer:"));
        formPanel.add(volunteerCombo);
        formPanel.add(createLabel("Start Date (MM-DD-YYYY):"));
        formPanel.add(startDateField);
        formPanel.add(createLabel("End Date (MM-DD-YYYY):"));
        formPanel.add(endDateField);
        formPanel.add(createLabel("Status:"));
        formPanel.add(statusCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton createBtn = createModernButton("Create", GREEN);
        createBtn.addActionListener(e -> {
            try {
                // Extract volunteer ID from combo box selection
                String selected = (String) volunteerCombo.getSelectedItem();
                if (selected == null || selected.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please select a volunteer", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int volunteerId = Integer.parseInt(selected.split(" - ")[0]);
                
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                Timesheet ts = timesheetController.submit(
                    volunteerId,
                    LocalDate.parse(startDateField.getText(), inputFormatter),
                    LocalDate.parse(endDateField.getText(), inputFormatter),
                    (TimesheetStatus) statusCombo.getSelectedItem()
                );
                JOptionPane.showMessageDialog(dialog, 
                    "Timesheet created successfully!\nID: " + ts.getTimesheetId() + "\nTotal Hours: " + ts.getTotalHours(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public void launch() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }
}
