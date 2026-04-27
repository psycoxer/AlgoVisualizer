package com.dsaviz.ui;

import com.dsaviz.core.Algorithm;
import com.dsaviz.core.GraphData;
import com.dsaviz.core.GraphEdge;
import com.dsaviz.core.GraphNode;
import com.dsaviz.algorithms.toc.TuringMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Visualization panel for Trees and Graphs.
 * Reads GraphData (nodes and edges) from the current algorithm.
 * Uses the currentState int[] to determine node colors.
 */
public class GraphTreeVisualizationPanel extends JPanel {

    private Algorithm algorithm;
    private int[] data; // Representing node states: 0=unvisited, 1=visited/processing, 2=active/current, 3=done/path
    private int highlightA = -1;
    private int highlightB = -1;
    private String action = "";
    private boolean completed = false;

    // Colors
    private static final Color BG              = new Color(22, 27, 44);
    private static final Color NODE_DEFAULT    = new Color(35, 45, 65);
    private static final Color NODE_VISITED    = new Color(0, 206, 201);
    private static final Color NODE_ACTIVE     = new Color(255, 107, 53);
    private static final Color NODE_DONE       = new Color(0, 230, 118);
    private static final Color NODE_TEXT       = Color.WHITE;
    
    private static final Color EDGE_DEFAULT    = new Color(60, 70, 90);
    private static final Color EDGE_ACTIVE     = new Color(255, 20, 147);
    private static final Color EDGE_TEXT       = new Color(180, 190, 210);
    private static final Color OUTLINE         = new Color(60, 65, 90);

    // Dragging state
    private GraphNode draggedNode = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private JScrollPane tapeScrollPane;
    private TapePanel tapePanel;

    public GraphTreeVisualizationPanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (algorithm == null || algorithm.getGraphData() == null) return;
                int r = 18;
                for (GraphNode node : algorithm.getGraphData().getNodes()) {
                    int dx = e.getX() - node.getX();
                    int dy = e.getY() - node.getY();
                    if (dx * dx + dy * dy <= r * r) {
                        draggedNode = node;
                        dragOffsetX = node.getX() - e.getX();
                        dragOffsetY = node.getY() - e.getY();
                        break;
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX() + dragOffsetX);
                    draggedNode.setY(e.getY() + dragOffsetY);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        };

        addMouseListener(dragAdapter);
        addMouseMotionListener(dragAdapter);

        tapePanel = new TapePanel();
        tapeScrollPane = new JScrollPane(tapePanel);
        tapeScrollPane.setPreferredSize(new Dimension(0, 110)); 
        tapeScrollPane.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, OUTLINE));
        tapeScrollPane.setBackground(BG);
        tapeScrollPane.getViewport().setBackground(BG);
        tapeScrollPane.setVisible(false); 
        tapeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tapeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(tapeScrollPane, BorderLayout.SOUTH);
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        boolean isTM = (algorithm instanceof TuringMachine);
        if (tapeScrollPane.isVisible() != isTM) {
            tapeScrollPane.setVisible(isTM);
            revalidate();
        }
        repaint();
    }

    public void updateState(int[] data, int indexA, int indexB, String action) {
        this.data = data;
        this.highlightA = indexA;
        this.highlightB = indexB;
        this.action = action;
        this.completed = false;
        
        if (algorithm instanceof TuringMachine tm) {
            tapePanel.revalidate();
            tapePanel.repaint();
            // Autoscroll logic inside invokeLater to ensure viewport has updated bounds
            SwingUtilities.invokeLater(() -> {
                if (tm.getTape() == null) return;
                int targetX = tm.getHeadPos() * TapePanel.CELL_WIDTH;
                int viewWidth = tapeScrollPane.getViewport().getWidth();
                int scrollX = targetX - (viewWidth / 2) + (TapePanel.CELL_WIDTH / 2);
                scrollX = Math.max(0, scrollX);
                int maxScroll = Math.max(0, tapePanel.getPreferredSize().width - viewWidth);
                scrollX = Math.min(scrollX, maxScroll);
                tapeScrollPane.getViewport().setViewPosition(new Point(scrollX, 0));
            });
        }
        
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
        completed = true; 
        this.highlightA = -1; 
        this.highlightB = -1; 
        if (algorithm == null || algorithm.getCategory() != com.dsaviz.core.AlgorithmCategory.TOC) {
            this.action = "Complete ✓"; 
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Action text
        if (action != null && !action.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(220, 220, 230));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(action, (getWidth() - fm.stringWidth(action)) / 2, 30);
        }

        if (algorithm == null || algorithm.getGraphData() == null || algorithm.getGraphData().getNodes().isEmpty()) {
            drawEmpty(g2);
            return;
        }

        GraphData graphData = algorithm.getGraphData();
        
        // Draw edges first (so they are under nodes)
        g2.setStroke(new BasicStroke(2.0f));
        for (GraphEdge edge : graphData.getEdges()) {
            drawEdge(g2, edge, graphData);
        }

        // Draw nodes
        for (GraphNode node : graphData.getNodes()) {
            drawNode(g2, node);
        }

        if (completed && algorithm != null && algorithm.getCategory() == com.dsaviz.core.AlgorithmCategory.TOC) {
            String res = (action != null && action.contains("ACCEPTED")) ? "ACCEPTED" : "REJECTED";
            g2.setFont(new Font("SansSerif", Font.BOLD, 64));
            g2.setColor(res.equals("ACCEPTED") ? new Color(0, 230, 118, 200) : new Color(255, 60, 80, 200));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(res, (getWidth() - fm.stringWidth(res)) / 2, getHeight() / 2);
        }
    }

    private class TapePanel extends JPanel {
        public static final int CELL_WIDTH = 50;
        public static final int CELL_HEIGHT = 50;

        public TapePanel() {
            setBackground(BG);
        }

        @Override
        public Dimension getPreferredSize() {
            if (algorithm instanceof TuringMachine tm && tm.getTape() != null) {
                return new Dimension(tm.getTape().length * CELL_WIDTH, CELL_HEIGHT + 40);
            }
            return new Dimension(0, CELL_HEIGHT + 40);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!(algorithm instanceof TuringMachine tm) || tm.getTape() == null) return;
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            char[] tape = tm.getTape();
            int head = tm.getHeadPos();
            
            g2.setFont(new Font("Monospaced", Font.BOLD, 24));
            FontMetrics fm = g2.getFontMetrics();

            int y = 20;

            for (int i = 0; i < tape.length; i++) {
                int x = i * CELL_WIDTH;
                
                // Draw cell background
                g2.setColor(i == head ? NODE_ACTIVE : NODE_DEFAULT);
                g2.fillRect(x, y, CELL_WIDTH, CELL_HEIGHT);
                
                g2.setColor(OUTLINE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(x, y, CELL_WIDTH, CELL_HEIGHT);
                
                // Draw char (replace '_' with blank space for clarity if desired, but '_' helps see bounds)
                String s = String.valueOf(tape[i]);
                g2.setColor(i == head ? Color.WHITE : EDGE_TEXT);
                g2.drawString(s, x + (CELL_WIDTH - fm.stringWidth(s)) / 2, y + (CELL_HEIGHT + fm.getAscent() - fm.getDescent()) / 2);
                
                // Draw Head pointer triangle
                if (i == head) {
                    g2.setColor(NODE_ACTIVE);
                    int[] px = {x + CELL_WIDTH/2 - 8, x + CELL_WIDTH/2 + 8, x + CELL_WIDTH/2};
                    int[] py = {y + CELL_HEIGHT + 14, y + CELL_HEIGHT + 14, y + CELL_HEIGHT + 4};
                    g2.fillPolygon(px, py, 3);
                }
            }
        }
    }

    private void drawEdge(Graphics2D g2, GraphEdge edge, GraphData graphData) {
        GraphNode source = null;
        GraphNode target = null;
        for (GraphNode n : graphData.getNodes()) {
            if (n.getId() == edge.getSourceId()) source = n;
            if (n.getId() == edge.getTargetId()) target = n;
        }
        
        if (source == null || target == null) return;

        boolean isActiveEdge = (!completed) && 
            ((source.getId() == highlightA && target.getId() == highlightB) ||
             (source.getId() == highlightB && target.getId() == highlightA));

        g2.setColor(isActiveEdge ? EDGE_ACTIVE : EDGE_DEFAULT);
        
        int x1 = source.getX(), y1 = source.getY();
        int x2 = target.getX(), y2 = target.getY();

        // Self-loop
        if (source.getId() == target.getId()) {
            int r = 18;
            g2.drawArc(x1 - r, y1 - r * 3, r * 2, r * 2, -30, 240);
            if (edge.isDirected()) {
                drawArrowHead(g2, x1, y1 - r, Math.PI / 2.0, isActiveEdge ? EDGE_ACTIVE : EDGE_DEFAULT);
            }
            if (edge.getLabel() != null && !edge.getLabel().isEmpty()) {
                g2.setColor(EDGE_TEXT);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString(edge.getLabel(), x1 - 5, y1 - r * 3 - 5);
            }
            return;
        }

        // Check for reverse edge to offset
        boolean hasReverse = false;
        for (GraphEdge e : graphData.getEdges()) {
            if (e.getSourceId() == target.getId() && e.getTargetId() == source.getId()) {
                hasReverse = true;
                break;
            }
        }

        // Calculate original angle before translation for arrow
        double angle = Math.atan2(y2 - y1, x2 - x1);

        if (hasReverse) {
            double dx = x2 - x1, dy = y2 - y1;
            double len = Math.hypot(dx, dy);
            // normal vector
            double nx = -dy / len * 6; // 6 px offset
            double ny = dx / len * 6;
            x1 += nx; y1 += ny;
            x2 += nx; y2 += ny;
        }

        g2.drawLine(x1, y1, x2, y2);

        int tipX = (int) (x2 - 18 * Math.cos(angle));
        int tipY = (int) (y2 - 18 * Math.sin(angle));

        if (edge.isDirected()) {
            drawArrowHead(g2, tipX, tipY, angle, isActiveEdge ? EDGE_ACTIVE : EDGE_DEFAULT);
        }

        if (edge.getLabel() != null && !edge.getLabel().isEmpty()) {
            g2.setColor(EDGE_TEXT);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;
            if (hasReverse) {
                double dx = x2 - x1, dy = y2 - y1;
                double len = Math.hypot(dx, dy);
                mx += -dy / len * 12;
                my += dx / len * 12;
            }
            g2.drawString(edge.getLabel(), mx + 5, my - 5);
        }
    }

    private void drawArrowHead(Graphics2D g2, int tipX, int tipY, double angle, Color c) {
        int arrowSize = 6;
        Graphics2D gCpy = (Graphics2D) g2.create();
        gCpy.translate(tipX, tipY);
        gCpy.rotate(angle);
        gCpy.setColor(c);
        
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0); // Tip
        arrowHead.addPoint(-10, -5); // Back Left
        arrowHead.addPoint(-10, 5); // Back Right
        gCpy.fill(arrowHead);
        gCpy.dispose();
    }

    private void drawNode(Graphics2D g2, GraphNode node) {
        int r = 18; // radius
        int d = r * 2;
        int x = node.getX() - r;
        int y = node.getY() - r;

        Color bg = NODE_DEFAULT;
        if (completed) {
            bg = NODE_DONE;
        } else if (data != null && node.getId() < data.length) {
            int state = data[node.getId()];
            if (node.getId() == highlightA || node.getId() == highlightB) bg = NODE_ACTIVE;
            else if (state == 3) bg = NODE_DONE;
            else if (state == 2) bg = NODE_ACTIVE;
            else if (state == 1) bg = NODE_VISITED;
        }

        // Glow if active
        if (!completed && (node.getId() == highlightA || node.getId() == highlightB)) {
            g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 80));
            g2.fillOval(x - 4, y - 4, d + 8, d + 8);
        }

        g2.setColor(bg);
        g2.fillOval(x, y, d, d);

        g2.setColor(OUTLINE);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(x, y, d, d);
        
        // Draw double circle if it's a final state (ToC)
        if (node.isFinalState()) {
            g2.drawOval(x + 4, y + 4, d - 8, d - 8);
        }
        
        // Draw start state incoming arrow (ToC)
        if (node.isStartState()) {
            g2.drawLine(x - 40, y + r, x, y + r);
            drawArrowHead(g2, x, y + r, 0.0, OUTLINE);
        }

        g2.setColor(NODE_TEXT);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(node.getLabel(), node.getX() - fm.stringWidth(node.getLabel()) / 2, node.getY() + 5);
    }

    private void drawEmpty(Graphics2D g2) {
        g2.setColor(new Color(100, 100, 120));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String text = "Generate a graph/tree to begin";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2);
    }
}
