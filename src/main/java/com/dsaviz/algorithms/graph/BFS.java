package com.dsaviz.algorithms.graph;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.*;

public class BFS extends Algorithm {

    public BFS() {
        this.name = "Breadth-First Search (BFS)";
        this.description = "Explores a graph level by level using a Queue. "
                + "It visits all direct neighbors of a node before moving to the next level. "
                + "Unweighted edges are used.";
        this.timeComplexity = "O(V + E)";
        this.spaceComplexity = "O(V)";
        this.category = AlgorithmCategory.GRAPH;
    }

    @Override
    public void generateStructure(int size) {
        graphData.clear();
        int n = Math.min(size, 15); // Cap at 15 for visual clarity
        
        // Generate nodes in a grid-like or random circular layout
        int cx = 400, cy = 250, r = 150;
        for (int i = 0; i < n; i++) {
            int x = cx + (int)(r * Math.cos(2 * Math.PI * i / n));
            int y = cy + (int)(r * Math.sin(2 * Math.PI * i / n));
            // Add a bit of jitter
            x += (Math.random() - 0.5) * 40;
            y += (Math.random() - 0.5) * 40;
            graphData.addNode(new GraphNode(i, x, y, String.valueOf(i)));
        }

        // Generate edges (ensure connected)
        for (int i = 0; i < n - 1; i++) {
            graphData.addEdge(new GraphEdge(i, i + 1, "", false));
        }
        // Add random edges
        for (int i = 0; i < n * 1.5; i++) {
            int u = (int)(Math.random() * n);
            int v = (int)(Math.random() * n);
            if (u != v) graphData.addEdge(new GraphEdge(u, v, "", false));
        }
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = graphData.getNodes().size();
        if (n == 0) return;

        int[] state = new int[n]; // 0=unvisited, 1=queued, 2=active, 3=done
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();

        int startNode = 0;
        queue.add(startNode);
        visited[startNode] = true;
        state[startNode] = 1;

        step(state.clone(), -1, -1, "Start at node " + startNode, callback);

        while (!queue.isEmpty() && !cancelled) {
            int u = queue.poll();
            state[u] = 2; // Active
            steps++;
            step(state.clone(), u, -1, "Dequeued node " + u, callback);

            // Find neighbors
            for (GraphEdge edge : graphData.getEdges()) {
                if (cancelled) break;
                
                int v = -1;
                if (edge.getSourceId() == u) v = edge.getTargetId();
                else if (!edge.isDirected() && edge.getTargetId() == u) v = edge.getSourceId();

                if (v != -1) {
                    comparisons++;
                    step(state.clone(), u, v, "Checking neighbor " + v, callback);

                    if (!visited[v]) {
                        visited[v] = true;
                        state[v] = 1; // Queued
                        queue.add(v);
                        step(state.clone(), u, v, "Enqueue node " + v, callback);
                    }
                }
            }
            state[u] = 3; // Done
            step(state.clone(), u, -1, "Finished node " + u, callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
