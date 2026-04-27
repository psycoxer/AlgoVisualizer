package com.dsaviz.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

/**
 * Control bar with Generate / Start / Pause / Reset buttons,
 * plus array-size and speed sliders, and a search-target field.
 */
public class ControlPanel extends JPanel {

    // --- Exposed controls ---
    private final JButton btnGenerate;
    private final JButton btnCustomInput;
    private final JButton btnStart;
    private final JButton btnStep;
    private final JButton btnPause;
    private final JButton btnReset;
    private final JSlider sliderSize;
    private final JSlider sliderSpeed;
    private final JTextField txtSearchTarget;
    private final JPanel searchPanel;

    // --- Colors ---
    private static final Color PANEL_BG    = new Color(20, 24, 40);
    private static final Color BTN_PRIMARY = new Color(0, 180, 230);
    private static final Color BTN_DANGER  = new Color(220, 60, 80);
    private static final Color BTN_WARN    = new Color(245, 166, 35);
    private static final Color BTN_OK      = new Color(0, 200, 100);
    private static final Color TEXT_FG     = new Color(220, 220, 235);

    public ControlPanel() {
        setBackground(PANEL_BG);
        setBorder(new EmptyBorder(10, 16, 10, 16));
        setLayout(new FlowLayout(FlowLayout.CENTER, 14, 6));
        setPreferredSize(new Dimension(0, 70));

        // --- Buttons ---
        btnGenerate = styledButton("⟳ Generate", BTN_PRIMARY);
        btnCustomInput = styledButton("✎ Custom", new Color(138, 43, 226)); // Purple
        btnStart    = styledButton("▶ Start",    BTN_OK);
        btnStep     = styledButton("⏭ Step",     new Color(70, 130, 180)); // Steel Blue
        btnPause    = styledButton("⏸ Pause",    BTN_WARN);
        btnReset    = styledButton("■ Reset",    BTN_DANGER);
        btnPause.setEnabled(false);
        btnReset.setEnabled(false);
        btnStep.setEnabled(false);

        add(btnGenerate);
        add(btnCustomInput);
        add(btnStart);
        add(btnStep);
        add(btnPause);
        add(btnReset);

        // --- Separator ---
        add(createSeparator());

        // --- Size slider ---
        add(sliderLabel("Size:"));
        sliderSize = new JSlider(10, 200, 50);
        sliderSize.setBackground(PANEL_BG);
        sliderSize.setForeground(TEXT_FG);
        sliderSize.setPreferredSize(new Dimension(120, 30));
        sliderSize.setToolTipText("Array size");
        add(sliderSize);

        // --- Speed slider ---
        add(sliderLabel("Speed:"));
        sliderSpeed = new JSlider(1, 500, 50);
        sliderSpeed.setBackground(PANEL_BG);
        sliderSpeed.setForeground(TEXT_FG);
        sliderSpeed.setPreferredSize(new Dimension(120, 30));
        sliderSpeed.setToolTipText("Delay (ms) — lower = faster");
        sliderSpeed.setInverted(true);  // left = fast, right = slow
        add(sliderSpeed);

        // --- Search target (hidden by default) ---
        add(createSeparator());
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchPanel.setBackground(PANEL_BG);
        searchPanel.add(sliderLabel("Target:"));
        txtSearchTarget = new JTextField(5);
        txtSearchTarget.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchPanel.add(txtSearchTarget);
        searchPanel.setVisible(false);
        add(searchPanel);
    }

    // --- Public API ---

    public JButton getGenerateButton() { return btnGenerate; }
    public JButton getCustomInputButton() { return btnCustomInput; }
    public JButton getStartButton()    { return btnStart; }
    public JButton getStepButton()     { return btnStep; }
    public JButton getPauseButton()    { return btnPause; }
    public JButton getResetButton()    { return btnReset; }
    public JSlider getSizeSlider()     { return sliderSize; }
    public JSlider getSpeedSlider()    { return sliderSpeed; }

    public void setSearchMode(boolean enabled) {
        searchPanel.setVisible(enabled);
        revalidate();
    }

    public void setSearchLabel(String text) {
        // Find the label in searchPanel and update its text
        for (Component c : searchPanel.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setText(text);
                break;
            }
        }
    }

    public String getSearchTargetString() {
        return txtSearchTarget.getText().trim();
    }

    public int getSearchTarget() {
        try {
            return Integer.parseInt(getSearchTargetString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getArraySize() {
        return sliderSize.getValue();
    }

    public int getSpeedDelay() {
        return sliderSpeed.getValue();
    }

    /** Adjust the size slider range (e.g., cap for stack/queue). */
    public void setSizeRange(int min, int max) {
        sliderSize.setMinimum(min);
        sliderSize.setMaximum(max);
        if (sliderSize.getValue() > max) sliderSize.setValue(max);
        if (sliderSize.getValue() < min) sliderSize.setValue(min);
    }

    public void setRunningState(boolean running) {
        btnGenerate.setEnabled(!running);
        btnStart.setEnabled(!running);
        btnPause.setEnabled(running);
        btnReset.setEnabled(running);
        sliderSize.setEnabled(!running);
    }

    public void setIdleState() {
        btnGenerate.setEnabled(true);
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnReset.setEnabled(false);
        sliderSize.setEnabled(true);
        btnPause.setText("⏸ Pause");
    }

    public void togglePauseText(boolean paused) {
        btnPause.setText(paused ? "▶ Resume" : "⏸ Pause");
    }

    // --- Helpers ---

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        return btn;
    }

    private JLabel sliderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_FG);
        return lbl;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(new Color(60, 60, 80));
        return sep;
    }
}
