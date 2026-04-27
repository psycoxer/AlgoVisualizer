package com.dsaviz.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomInputDialog extends JDialog {

    private JTextArea textArea;
    private boolean isGraphMode;
    private String resultText = null;
    private boolean confirmed = false;

    public CustomInputDialog(Frame owner, boolean isGraphMode) {
        super(owner, "Custom Input", true);
        this.isGraphMode = isGraphMode;

        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(22, 27, 44));

        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setOpaque(false);

        // Header label
        JLabel lblInstruct = new JLabel("<html><body style='color:#DDD;'><b>Enter Custom Input</b><br>" 
                + (isGraphMode ? "Enter edges as <code>source,target,weight</code> (one per line).<br>Example:<br>0,1,5<br>1,2,10" 
                               : "Enter comma or space separated numbers.<br>Example: <code>42, 15, 8, 99</code>") 
                + "</body></html>");
        lblInstruct.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mainPanel.add(lblInstruct, BorderLayout.NORTH);

        // Text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(35, 45, 65));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 90)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel, new Color(80, 85, 100));
        btnCancel.addActionListener(e -> dispose());

        JButton btnOk = new JButton("Apply");
        styleButton(btnOk, new Color(0, 200, 100));
        btnOk.addActionListener(e -> {
            resultText = textArea.getText().trim();
            confirmed = true;
            dispose();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnOk);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(80, 30));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getResultText() {
        return resultText;
    }
}
