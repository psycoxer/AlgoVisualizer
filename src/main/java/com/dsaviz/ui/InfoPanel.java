package com.dsaviz.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Displays real-time statistics and algorithm metadata.
 */
public class InfoPanel extends JPanel {

    private final JLabel lblAlgorithm;
    private final JTextArea txtDescription;
    private final JLabel lblTimeComplexity;
    private final JLabel lblSpaceComplexity;
    private final JLabel lblComparisons;
    private final JLabel lblSwaps;
    private final JLabel lblSteps;
    private final JLabel lblStatus;

    private static final Color PANEL_BG   = new Color(26, 26, 46);
    private static final Color ACCENT     = new Color(0, 210, 255);
    private static final Color LABEL_FG   = new Color(160, 160, 180);
    private static final Color VALUE_FG   = new Color(230, 230, 245);

    public InfoPanel() {
        setBackground(PANEL_BG);
        setBorder(new EmptyBorder(15, 14, 15, 14));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Algorithm name
        lblAlgorithm = styledLabel("—", 16, Font.BOLD, ACCENT);
        add(lblAlgorithm);
        add(Box.createVerticalStrut(10));

        // Description
        txtDescription = new JTextArea("Select an algorithm to begin.");
        txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDescription.setForeground(LABEL_FG);
        txtDescription.setBackground(PANEL_BG);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setFocusable(false);
        txtDescription.setAlignmentX(LEFT_ALIGNMENT);
        txtDescription.setMaximumSize(new Dimension(200, 100));
        add(txtDescription);
        add(Box.createVerticalStrut(16));

        // Complexity
        add(sectionHeader("COMPLEXITY"));
        add(Box.createVerticalStrut(6));
        lblTimeComplexity = statRow("Time");
        lblSpaceComplexity = statRow("Space");
        add(Box.createVerticalStrut(16));

        // Statistics
        add(sectionHeader("STATISTICS"));
        add(Box.createVerticalStrut(6));
        lblComparisons = statRow("Comparisons");
        lblSwaps = statRow("Swaps");
        lblSteps = statRow("Steps");
        add(Box.createVerticalStrut(16));

        // Status
        add(sectionHeader("STATUS"));
        add(Box.createVerticalStrut(6));
        lblStatus = styledLabel("Idle", 13, Font.BOLD, new Color(0, 230, 118));
        add(lblStatus);

        add(Box.createVerticalGlue());
    }

    // --- Public API ---

    public void setAlgorithmInfo(String name, String description,
                                 String timeComplexity, String spaceComplexity) {
        lblAlgorithm.setText(name);
        txtDescription.setText(description);
        lblTimeComplexity.setText("Time:  " + timeComplexity);
        lblSpaceComplexity.setText("Space: " + spaceComplexity);
        resetStats();
    }

    public void updateStats(int comparisons, int swaps, int steps) {
        lblComparisons.setText("Comparisons: " + comparisons);
        lblSwaps.setText("Swaps: " + swaps);
        lblSteps.setText("Steps: " + steps);
    }

    public void setStatus(String status, Color color) {
        lblStatus.setText(status);
        lblStatus.setForeground(color);
    }

    public void resetStats() {
        lblComparisons.setText("Comparisons: 0");
        lblSwaps.setText("Swaps: 0");
        lblSteps.setText("Steps: 0");
        setStatus("Idle", new Color(160, 160, 180));
    }

    // --- Helpers ---

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 100, 130));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel statRow(String label) {
        JLabel lbl = styledLabel(label + ": 0", 13, Font.PLAIN, VALUE_FG);
        return lbl;
    }

    private JLabel styledLabel(String text, int size, int style, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", style, size));
        lbl.setForeground(fg);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        add(lbl);               // auto-add to this panel
        return lbl;
    }
}
