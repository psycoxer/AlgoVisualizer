package com.dsaviz.algorithms.graph;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class DFS extends Algorithm {

    public DFS() {
        this.name = "Depth-First Search (DFS)";
        this.description = "Explores a graph by going as deep as possible along each branch "
                + "before backtracking. Uses a Stack (call stack) implicitly. "
                + "Unweighted edges are used.";
        this.timeComplexity = "O(V + E)";
        this.spaceComplexity = "O(V)";
        this.category = AlgorithmCategory.GRAPH;
    }

    @Override
    public void generateStructure(int size) {
        graphData.clear();
        int n = Math.min(size, 15);
        int cx = 400, cy = 250, r = 150;
        for (int i = 0; i < n; i++) {
            int x = cx + (int)(r * Math.cos(2 * Math.PI * i / n));
            int y = cy + (int)(r * Math.sin(2 * Math.PI * i / n));
            x += (Math.random() - 0.5) * 40;
            y += (Math.random() - 0.5) * 40;
            graphData.addNode(new GraphNode(i, x, y, String.valueOf(i)));
        }
        for (int i = 0; i < n - 1; i++) {
            graphData.addEdge(new GraphEdge(i, i + 1, "", false));
        }
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

        int[] state = new int[n]; // 0=unvisited, 1=discovered, 2=active, 3=done
        boolean[] visited = new boolean[n];

        dfsRecursive(0, state, visited, callback);

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private void dfsRecursive(int u, int[] state, boolean[] visited, AlgorithmCallback callback) {
        if (cancelled) return;

        visited[u] = true;
        state[u] = 2; // Active
        steps++;
        step(state.clone(), u, -1, "Visiting node " + u, callback);

        for (GraphEdge edge : graphData.getEdges()) {
            if (cancelled) break;
            int v = -1;
            if (edge.getSourceId() == u) v = edge.getTargetId();
            else if (!edge.isDirected() && edge.getTargetId() == u) v = edge.getSourceId();

            if (v != -1) {
                comparisons++;
                step(state.clone(), u, v, "Checking edge to " + v, callback);

                if (!visited[v]) {
                    state[v] = 1;
                    step(state.clone(), u, v, "Going deeper to node " + v, callback);
                    dfsRecursive(v, state, visited, callback);
                    
                    if (cancelled) return;
                    state[u] = 2; // Back to this node
                    step(state.clone(), u, v, "Backtracking from " + v + " to " + u, callback);
                }
            }
        }
        state[u] = 3; // Done
        step(state.clone(), u, -1, "Finished node " + u, callback);
    }
}
