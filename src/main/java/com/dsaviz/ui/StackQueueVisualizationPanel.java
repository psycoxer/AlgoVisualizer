package com.dsaviz.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualization panel for Stack and Queue data structures.
 * Stack: vertical blocks (bottom → top).
 * Queue: horizontal blocks (left → right).
 *
 * When blocks become too small to display text, the value is
 * hidden and shown via tooltip on mouse hover instead.
 */
public class StackQueueVisualizationPanel extends JPanel {

    private int[] data;
    private int highlightIndex = -1;
    private String action = "";
    private boolean stackMode = true;
    private boolean completed = false;

    /** Rectangles for each block – rebuilt on every paint for hover detection. */
    private final List<Rectangle> blockRects = new ArrayList<>();

    // Size thresholds
    private static final int STACK_MAX   = 40;   // max visible stack blocks
    private static final int QUEUE_MAX   = 50;   // max visible queue blocks
    private static final int MIN_TEXT_H  = 22;   // stack: hide text if blockH < this
    private static final int MIN_TEXT_W  = 36;   // queue: hide text if blockW < this

    // Colors
    private static final Color BG              = new Color(22, 27, 44);
    private static final Color STACK_START      = new Color(255, 107, 53);
    private static final Color STACK_END        = new Color(255, 20, 147);
    private static final Color QUEUE_START      = new Color(0, 206, 201);
    private static final Color QUEUE_END        = new Color(108, 92, 231);
    private static final Color HIGHLIGHT_CLR    = new Color(233, 69, 96);
    private static final Color DONE_CLR         = new Color(0, 230, 118);
    private static final Color OUTLINE          = new Color(60, 65, 90);
    private static final Color LABEL_CLR        = new Color(0, 210, 255);

    public StackQueueVisualizationPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(800, 500));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tooltip on hover – show value when text is hidden (or always)
        ToolTipManager.sharedInstance().setInitialDelay(100);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setToolTipText(null); // clear first
                if (data == null) return;
                for (int i = 0; i < blockRects.size(); i++) {
                    if (blockRects.get(i).contains(e.getPoint())) {
                        setToolTipText("Value: " + data[i]);
                        return;
                    }
                }
            }
        });
    }

    public void setStackMode(boolean stackMode) { this.stackMode = stackMode; repaint(); }

    public void updateState(int[] data, int indexA, int indexB, String action) {
        this.data = data;
        this.highlightIndex = indexA;
        this.action = action;
        this.completed = false;
        repaint();
    }

    public void setData(int[] data) {
        this.data = data; highlightIndex = -1; action = ""; completed = false; repaint();
    }

    public void markCompleted() {
        completed = true; highlightIndex = -1; action = "Complete ✓"; repaint();
    }

    public void clearState() {
        data = null; highlightIndex = -1; action = ""; completed = false; repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        blockRects.clear();

        // Action text at top
        if (action != null && !action.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(220, 220, 230));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(action, (getWidth() - fm.stringWidth(action)) / 2, 30);
        }

        if (data == null || data.length == 0) {
            drawEmpty(g2);
            return;
        }

        if (stackMode) drawStack(g2); else drawQueue(g2);
    }

    // ── Stack rendering (vertical, bottom-to-top) ──────────────────
    private void drawStack(Graphics2D g2) {
        int n = Math.min(data.length, STACK_MAX);
        int blockW = 140;
        int availableH = getHeight() - 140;
        int blockH = Math.max(6, Math.min(50, availableH / Math.max(1, n)));
        int gap = blockH > 12 ? 3 : 1;
        int cx = getWidth() / 2;
        int baseY = getHeight() - 55;
        boolean showText = blockH >= MIN_TEXT_H;

        // Base plate
        g2.setColor(OUTLINE);
        g2.fillRoundRect(cx - blockW / 2 - 12, baseY, blockW + 24, 6, 4, 4);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(100, 100, 130));
        drawCentered(g2, "STACK BASE", cx, baseY + 22);

        // Overflow indicator
        if (data.length > STACK_MAX) {
            g2.setColor(new Color(180, 120, 60));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            drawCentered(g2, "(" + (data.length - STACK_MAX) + " more below…)", cx, baseY + 36);
        }

        for (int i = 0; i < n; i++) {
            int x = cx - blockW / 2;
            int y = baseY - (i + 1) * (blockH + gap);
            Color c = blockColor(i, n, true);

            blockRects.add(new Rectangle(x, y, blockW, blockH));

            // Glow
            if (!completed && i == highlightIndex) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                g2.fillRoundRect(x - 4, y - 4, blockW + 8, blockH + 8, 14, 14);
            }

            g2.setColor(c);
            g2.fillRoundRect(x, y, blockW, blockH, blockH > 14 ? 10 : 4, blockH > 14 ? 10 : 4);

            // Value text — only if the block is tall enough
            if (showText) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.min(14, blockH - 8)));
                drawCentered(g2, String.valueOf(data[i]), cx, y + blockH / 2 + 5);
            }

            // TOP label
            if (i == n - 1) {
                g2.setColor(LABEL_CLR);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("TOP →", x - 62, y + blockH / 2 + 5);
            }
        }

        // Hint when text is hidden
        if (!showText && n > 0) {
            g2.setColor(new Color(120, 120, 150));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            drawCentered(g2, "Hover over a block to see its value", cx, 50);
        }
    }

    // ── Queue rendering (horizontal, left-to-right) ────────────────
    private void drawQueue(Graphics2D g2) {
        int n = Math.min(data.length, QUEUE_MAX);
        int pad = 80;
        int availableW = getWidth() - 2 * pad;
        int blockW = Math.max(8, Math.min(80, availableW / Math.max(1, n)));
        int blockH = 60;
        int gap = blockW > 20 ? 4 : 1;
        int totalW = n * (blockW + gap) - gap;
        int startX = (getWidth() - totalW) / 2;
        int cy = getHeight() / 2;
        boolean showText = blockW >= MIN_TEXT_W;

        // Overflow indicator
        if (data.length > QUEUE_MAX) {
            g2.setColor(new Color(180, 120, 60));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            drawCentered(g2, "(" + (data.length - QUEUE_MAX) + " more to the right…)",
                    getWidth() / 2, cy + blockH / 2 + 30);
        }

        for (int i = 0; i < n; i++) {
            int x = startX + i * (blockW + gap);
            int y = cy - blockH / 2;
            Color c = blockColor(i, n, false);

            blockRects.add(new Rectangle(x, y, blockW, blockH));

            if (!completed && i == highlightIndex) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                g2.fillRoundRect(x - 4, y - 4, blockW + 8, blockH + 8, 14, 14);
            }

            g2.setColor(c);
            g2.fillRoundRect(x, y, blockW, blockH, blockW > 14 ? 10 : 4, 10);

            // Value text — only if the block is wide enough
            if (showText) {
                g2.setColor(Color.WHITE);
                int fontSize = Math.min(14, blockW - 10);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, fontSize)));
                drawCentered(g2, String.valueOf(data[i]), x + blockW / 2, cy + 5);
            }
        }

        // FRONT / REAR labels
        if (n > 0) {
            g2.setColor(LABEL_CLR);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawCentered(g2, "FRONT", startX + blockW / 2, cy - blockH / 2 - 12);
            if (n > 1) {
                drawCentered(g2, "REAR", startX + totalW - blockW / 2, cy - blockH / 2 - 12);
            }
        }

        // Arrows between blocks (only when wide enough)
        if (blockW > 20) {
            g2.setColor(OUTLINE);
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < n - 1; i++) {
                int ax = startX + (i + 1) * (blockW + gap) - gap / 2;
                g2.drawLine(ax - 6, cy, ax + 6, cy);
                g2.drawLine(ax + 2, cy - 4, ax + 6, cy);
                g2.drawLine(ax + 2, cy + 4, ax + 6, cy);
            }
            g2.setStroke(new BasicStroke(1));
        }

        // Hint when text is hidden
        if (!showText && n > 0) {
            g2.setColor(new Color(120, 120, 150));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            drawCentered(g2, "Hover over a block to see its value",
                    getWidth() / 2, cy + blockH / 2 + 50);
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────
    private Color blockColor(int i, int n, boolean isStack) {
        if (completed) return DONE_CLR;
        if (i == highlightIndex) return HIGHLIGHT_CLR;
        float t = (float) i / Math.max(1, n - 1);
        Color s = isStack ? STACK_START : QUEUE_START;
        Color e = isStack ? STACK_END   : QUEUE_END;
        return new Color(
                (int)(s.getRed()   + t * (e.getRed()   - s.getRed())),
                (int)(s.getGreen() + t * (e.getGreen() - s.getGreen())),
                (int)(s.getBlue()  + t * (e.getBlue()  - s.getBlue())));
    }

    private void drawCentered(Graphics2D g2, String text, int cx, int cy) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, cx - fm.stringWidth(text) / 2, cy);
    }

    private void drawEmpty(Graphics2D g2) {
        g2.setColor(new Color(100, 100, 120));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String text = stackMode ? "Stack is empty" : "Queue is empty";
        drawCentered(g2, text, getWidth() / 2, getHeight() / 2);
    }
}
