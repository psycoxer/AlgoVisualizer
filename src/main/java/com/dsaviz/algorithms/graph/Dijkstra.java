package com.dsaviz.algorithms.graph;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.*;

public class Dijkstra extends Algorithm {

    public Dijkstra() {
        this.name = "Dijkstra's Shortest Path";
        this.description = "Finds the shortest paths between nodes in a graph. "
                + "Explores nodes with the smallest known distance first. "
                + "Weighted edges are used.";
        this.timeComplexity = "O(E log V)";
        this.spaceComplexity = "O(V)";
        this.category = AlgorithmCategory.GRAPH;
    }

    @Override
    public void generateStructure(int size) {
        graphData.clear();
        int n = Math.min(size, 10); // Keep it small so weights are readable
        int cx = 400, cy = 250, r = 150;
        for (int i = 0; i < n; i++) {
            int x = cx + (int)(r * Math.cos(2 * Math.PI * i / n));
            int y = cy + (int)(r * Math.sin(2 * Math.PI * i / n));
            x += (Math.random() - 0.5) * 40;
            y += (Math.random() - 0.5) * 40;
            graphData.addNode(new GraphNode(i, x, y, "∞"));
        }
        // Minimal spanning tree to ensure connected
        for (int i = 0; i < n - 1; i++) {
            int w = 1 + (int)(Math.random() * 9);
            graphData.addEdge(new GraphEdge(i, i + 1, String.valueOf(w), false));
        }
        for (int i = 0; i < n; i++) {
            int u = (int)(Math.random() * n);
            int v = (int)(Math.random() * n);
            if (u != v) {
                int w = 1 + (int)(Math.random() * 9);
                graphData.addEdge(new GraphEdge(u, v, String.valueOf(w), false));
            }
        }
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = graphData.getNodes().size();
        if (n == 0) return;

        int[] state = new int[n]; // 0=unvisited, 1=frontier, 2=active, 3=done
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        int startNode = 0;
        dist[startNode] = 0;
        pq.add(new int[]{startNode, 0});
        state[startNode] = 1;
        updateLabel(startNode, "0");

        step(state.clone(), -1, -1, "Start Dijkstra from node " + startNode, callback);

        while (!pq.isEmpty() && !cancelled) {
            int[] pair = pq.poll();
            int u = pair[0];
            int d = pair[1];

            if (state[u] == 3) continue; // Skip if already done
            if (d > dist[u]) continue; // Skip outdated entry

            state[u] = 2; // Active
            steps++;
            step(state.clone(), u, -1, "Processing min-distance node " + u + " (dist=" + d + ")", callback);

            for (GraphEdge edge : graphData.getEdges()) {
                if (cancelled) break;
                
                int v = -1;
                if (edge.getSourceId() == u) v = edge.getTargetId();
                else if (!edge.isDirected() && edge.getTargetId() == u) v = edge.getSourceId();

                if (v != -1) {
                    int weight = Integer.parseInt(edge.getLabel());
                    comparisons++;
                    step(state.clone(), u, v, "Checking edge to " + v + " (weight=" + weight + ")", callback);

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        state[v] = 1;
                        updateLabel(v, String.valueOf(dist[v]));
                        pq.add(new int[]{v, dist[v]});
                        swaps++; // Treating dist-update as swap for stats
                        step(state.clone(), u, v, "Updated shorter path to " + v + ": " + dist[v], callback);
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

    private void updateLabel(int id, String txt) {
        for (GraphNode n : graphData.getNodes()) {
            if (n.getId() == id) { n.setLabel(txt); break; }
        }
    }
}
