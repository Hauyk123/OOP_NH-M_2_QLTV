package org.example;

import javax.swing.SwingUtilities;

import org.example.UI.LoginUI;
import org.example.utils.FileUtils;

public class Main {

    public static void main(String[] args) {
        // Ensure all data files exist before starting
        FileUtils.ensureDataDirectoryExists();

        // Launch the LoginUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginUI();
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
