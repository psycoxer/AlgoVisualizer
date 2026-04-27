package com.dsaviz;

import com.formdev.flatlaf.FlatDarkLaf;
import com.dsaviz.ui.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 * Sets FlatLaf dark theme and launches the main window on the EDT.
 */
public class App {
    public static void main(String[] args) {
        // Install FlatLaf dark look and feel
        FlatDarkLaf.setup();

        // Global UI tweaks
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("TitlePane.unifiedBackground", true);

        // Launch on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
