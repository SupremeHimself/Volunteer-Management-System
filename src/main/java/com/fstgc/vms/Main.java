package com.fstgc.vms;

import com.fstgc.vms.repository.memory.InMemoryAdminRepository;
import com.fstgc.vms.service.AuthenticationService;
import com.fstgc.vms.ui.LoginDialog;
import com.fstgc.vms.ui.SystemUI;
import com.fstgc.vms.util.DataPersistence;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize data persistence
        DataPersistence.initialize();
        
        // Create authentication service
        AuthenticationService authService = new AuthenticationService(new InMemoryAdminRepository());
        
        // Show login dialog
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null, authService);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                SystemUI gui = new SystemUI(authService);
                gui.launch();
            } else {
                System.exit(0);
            }
        });
    }
}
