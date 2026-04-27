package com.dsaviz.ui;

import com.dsaviz.core.Algorithm;
import com.dsaviz.core.AlgorithmCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * Left-hand sidebar with algorithm categories and selection buttons.
 */
public class SidebarPanel extends JPanel {

    private Consumer<Algorithm> selectionCallback;
    private JButton selectedButton = null;

    private static final Color SIDEBAR_BG       = new Color(26, 26, 46);
    private static final Color CATEGORY_FG      = new Color(0, 210, 255);
    private static final Color BUTTON_BG        = new Color(35, 40, 60);
    private static final Color BUTTON_HOVER     = new Color(50, 55, 80);
    private static final Color BUTTON_SELECTED  = new Color(70, 50, 140);
    private static final Color BUTTON_FG        = new Color(220, 220, 235);

    public SidebarPanel(List<Algorithm> algorithms, Consumer<Algorithm> selectionCallback) {
        this.selectionCallback = selectionCallback;

        setLayout(new BorderLayout());
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Algorithms");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(10, 5, 20, 0));
        add(title, BorderLayout.NORTH);

        // Group algorithms by category
        Map<AlgorithmCategory, java.util.List<Algorithm>> grouped = new LinkedHashMap<>();
        for (Algorithm a : algorithms) {
            grouped.computeIfAbsent(a.getCategory(), k -> new java.util.ArrayList<>()).add(a);
        }

        // Build list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(SIDEBAR_BG);

        boolean first = true;
        for (var entry : grouped.entrySet()) {
            if (!first) listPanel.add(Box.createVerticalStrut(15));
            first = false;

            // Category header
            JLabel catLabel = new JLabel("▸ " + entry.getKey().getDisplayName());
            catLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            catLabel.setForeground(CATEGORY_FG);
            catLabel.setAlignmentX(LEFT_ALIGNMENT);
            catLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
            listPanel.add(catLabel);

            // Algorithm buttons
            for (Algorithm algo : entry.getValue()) {
                JButton btn = createAlgoButton(algo);
                btn.setAlignmentX(LEFT_ALIGNMENT);
                listPanel.add(btn);
                listPanel.add(Box.createVerticalStrut(4));
            }
        }

        listPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SIDEBAR_BG);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createAlgoButton(Algorithm algo) {
        JButton btn = new JButton(algo.getName());
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(BUTTON_FG);
        btn.setBackground(BUTTON_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(190, 36));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != selectedButton) btn.setBackground(BUTTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != selectedButton) btn.setBackground(BUTTON_BG);
            }
        });

        btn.addActionListener(e -> {
            if (selectedButton != null) {
                selectedButton.setBackground(BUTTON_BG);
            }
            selectedButton = btn;
            btn.setBackground(BUTTON_SELECTED);
            selectionCallback.accept(algo);
        });

        return btn;
    }
}
