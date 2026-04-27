package com.dsaviz.core;

/**
 * Represents a Node in a Graph or Tree.
 */
public class GraphNode {
    private int id;
    private int x;
    private int y;
    private String label;

    public GraphNode(int id, int x, int y, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    // ToC specific properties
    private boolean isFinalState = false;
    private boolean isStartState = false;
    
    public boolean isFinalState() { return isFinalState; }
    public void setFinalState(boolean isFinalState) { this.isFinalState = isFinalState; }
    
    public boolean isStartState() { return isStartState; }
    public void setStartState(boolean isStartState) { this.isStartState = isStartState; }
}
