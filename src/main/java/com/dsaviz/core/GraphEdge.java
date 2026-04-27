package com.dsaviz.core;

/**
 * Represents an Edge connecting two GraphNodes.
 */
public class GraphEdge {
    private int sourceId;
    private int targetId;
    private String label; // For weights, etc.
    private boolean directed;

    public GraphEdge(int sourceId, int targetId, String label, boolean directed) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.label = label;
        this.directed = directed;
    }

    public int getSourceId() { return sourceId; }
    public int getTargetId() { return targetId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public boolean isDirected() { return directed; }
}
