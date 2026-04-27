package com.dsaviz.algorithms.tree;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class BSTInsertSearch extends Algorithm {

    public BSTInsertSearch() {
        this.name = "BST Insert & Search";
        this.description = "Builds a Binary Search Tree by inserting random values, "
                + "then searches for a target value. Demonstrates logarithmic properties. "
                + "Left sub-tree < Root < Right sub-tree.";
        this.timeComplexity = "O(h) -> up to O(n) worst, O(log n) avg";
        this.spaceComplexity = "O(h)";
        this.category = AlgorithmCategory.TREE;
    }

    private class BSTNode {
        int id, value, x, y;
        BSTNode left, right;
        BSTNode(int id, int value) { this.id = id; this.value = value; }
    }
    
    private BSTNode root;
    private int nextId = 0;
    private int[] bstValues;

    @Override
    public void generateStructure(int size) {
        // Will be generated dynamically during execute()
        graphData.clear();
        root = null;
        nextId = 0;
        
        // Predetermine what to insert
        int n = Math.min(size, 15);
        bstValues = new int[n];
        for (int i=0; i<n; i++) bstValues[i] = (int)(Math.random() * 99) + 1;
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        
        int[] state = new int[200]; // arbitrarily large to hold node states
        
        step(state, -1, -1, "Starting BST construction with " + inData.length + " items.", callback);
        
        // Build Tree Visually
        for (int val : inData) {
            if (cancelled) return;
            root = insertVisual(root, val, 400, 80, 200, state, callback);
        }
        
        // Now SEARCH for a random value that is in the tree (or custom search target if set)
        int target = inData.length > 0 ? inData[(int)(Math.random() * inData.length)] : 999;
        // Or sometimes not in the tree (unless search target is explicitly passed via ControlPanel, handled in MainFrame)
        if (Math.random() > 0.8) target = 999;
        
        // Reset state array
        state = new int[nextId];
        step(state, -1, -1, "Tree built! Now searching for value: " + target, callback);
        
        BSTNode curr = root;
        boolean found = false;
        while (curr != null && !cancelled) {
            state[curr.id] = 2; // Active
            comparisons++;
            if (curr.value == target) {
                step(state.clone(), curr.id, -1, "Found target " + target + " at node " + curr.value + "!", callback);
                found = true;
                break;
            } else if (target < curr.value) {
                step(state.clone(), curr.id, -1, target + " < " + curr.value + " -> Go Left", callback);
                state[curr.id] = 1; // Visited
                curr = curr.left;
            } else {
                step(state.clone(), curr.id, -1, target + " > " + curr.value + " -> Go Right", callback);
                state[curr.id] = 1; // Visited
                curr = curr.right;
            }
        }
        
        if (!found && !cancelled) {
            step(state, -1, -1, "Target " + target + " not found in BST.", callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private BSTNode insertVisual(BSTNode node, int val, int x, int y, int xOffset, int[] state, AlgorithmCallback callback) {
        if (cancelled) return node;
        
        if (node == null) {
            // Found spot to insert!
            BSTNode n = new BSTNode(nextId++, val);
            n.x = x; n.y = y;
            graphData.addNode(new GraphNode(n.id, n.x, n.y, String.valueOf(n.value)));
            state[n.id] = 3; // Done
            swaps++; // Treating inserts as swaps for stats
            step(state.clone(), n.id, -1, "Inserted " + val, callback);
            return n;
        }

        state[node.id] = 2; // Active
        comparisons++;
        
        if (val < node.value) {
            step(state.clone(), node.id, -1, val + " < " + node.value + " -> Go Left", callback);
            
            // If we are about to insert a child, we need to draw the edge right after it's created
            boolean wasNull = (node.left == null);
            state[node.id] = 1; // Visited
            node.left = insertVisual(node.left, val, x - xOffset, y + 80, xOffset / 2, state, callback);
            if (wasNull && !cancelled && node.left != null) {
                graphData.addEdge(new GraphEdge(node.id, node.left.id, "", true));
            }
            
        } else {
            step(state.clone(), node.id, -1, val + " >= " + node.value + " -> Go Right", callback);
            
            boolean wasNull = (node.right == null);
            state[node.id] = 1; // Visited
            node.right = insertVisual(node.right, val, x + xOffset, y + 80, xOffset / 2, state, callback);
            if (wasNull && !cancelled && node.right != null) {
                graphData.addEdge(new GraphEdge(node.id, node.right.id, "", true));
            }
        }
        return node;
    }
}
