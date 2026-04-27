package com.dsaviz.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Custom panel that renders the array as animated vertical bars.
 * Bars are colored with a gradient; highlighted indices use accent colors.
 */
public class VisualizationPanel extends JPanel {

    // --- Color palette (dark-theme friendly) ---
    private static final Color BAR_START    = new Color(0, 210, 255);    // cyan
    private static final Color BAR_END      = new Color(123, 47, 247);   // purple
    private static final Color COMPARE_CLR  = new Color(233, 69, 96);    // coral-red
    private static final Color SWAP_CLR     = new Color(245, 166, 35);   // amber
    private static final Color DONE_CLR     = new Color(0, 230, 118);    // green
    private static final Color BG_COLOR     = new Color(22, 27, 44);     // deep navy

    // --- State ---
    private int[] data;
    private int highlightA = -1;
    private int highlightB = -1;
    private String action = "";
    private boolean completed = false;

    public VisualizationPanel() {
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(800, 500));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // -------------------------------------------------------
    //  Public API — called from the EDT
    // -------------------------------------------------------
    public void updateState(int[] data, int indexA, int indexB, String action) {
        this.data = data;
        this.highlightA = indexA;
        this.highlightB = indexB;
        this.action = action;
        this.completed = false;
        repaint();
    }

    public void setData(int[] data) {
        this.data = data;
        this.highlightA = -1;
        this.highlightB = -1;
        this.action = "";
        this.completed = false;
        repaint();
    }

    public void markCompleted() {
        this.completed = true;
        this.highlightA = -1;
        this.highlightB = -1;
        this.action = "Complete ✓";
        repaint();
    }

    public void clearState() {
        this.data = null;
        this.highlightA = -1;
        this.highlightB = -1;
        this.action = "";
        this.completed = false;
        repaint();
    }

    // -------------------------------------------------------
    //  Rendering
    // -------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (data == null || data.length == 0) {
            drawPlaceholder(g2);
            return;
        }

        int w = getWidth();
        int h = getHeight();
        int pad = 30;
        int topPad = 50;    // space for the action label at top
        int bottomPad = 20;

        int drawableW = w - 2 * pad;
        int drawableH = h - topPad - bottomPad;

        int n = data.length;
        double barW = (double) drawableW / n;
        int maxVal = Arrays.stream(data).max().orElse(1);

        // Draw bars
        for (int i = 0; i < n; i++) {
            int barH = (int) ((double) data[i] / maxVal * drawableH);
            int x = pad + (int) (i * barW);
            int y = topPad + drawableH - barH;

            Color barColor;
            if (completed) {
                barColor = DONE_CLR;
            } else if (i == highlightA) {
                barColor = COMPARE_CLR;
            } else if (i == highlightB) {
                barColor = SWAP_CLR;
            } else {
                float ratio = (float) i / Math.max(1, n - 1);
                barColor = interpolate(BAR_START, BAR_END, ratio);
            }

            // Draw bar with rounded top corners
            int bw = Math.max(1, (int) barW - (n > 100 ? 0 : 2));
            g2.setColor(barColor);
            g2.fillRoundRect(x, y, bw, barH, 4, 4);

            // Glow effect for highlighted bars
            if (!completed && (i == highlightA || i == highlightB)) {
                g2.setColor(new Color(barColor.getRed(), barColor.getGreen(),
                        barColor.getBlue(), 60));
                g2.fillRoundRect(x - 2, y - 2, bw + 4, barH + 4, 6, 6);
            }
        }

        // Draw action label
        if (action != null && !action.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(220, 220, 230));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(action);
            g2.drawString(action, (w - textW) / 2, 30);
        }
    }

    private void drawPlaceholder(Graphics2D g2) {
        g2.setColor(new Color(100, 100, 120));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String text = "Select an algorithm and click Generate to begin";
        FontMetrics fm = g2.getFontMetrics();
        int w = getWidth(), h = getHeight();
        g2.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2);
    }

    private static Color interpolate(Color c1, Color c2, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
                (int) (c1.getRed()   + t * (c2.getRed()   - c1.getRed())),
                (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen())),
                (int) (c1.getBlue()  + t * (c2.getBlue()  - c1.getBlue()))
        );
    }
}
