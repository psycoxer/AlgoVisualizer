package com.dsaviz.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the structure of a Graph or Tree for visualization.
 */
public class GraphData {
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphEdge> edges = new ArrayList<>();

    public void addNode(GraphNode node) {
        nodes.add(node);
    }

    public void addEdge(GraphEdge edge) {
        edges.add(edge);
    }

    public List<GraphNode> getNodes() { return nodes; }
    public List<GraphEdge> getEdges() { return edges; }
    
    public void clear() {
        nodes.clear();
        edges.clear();
    }
}
