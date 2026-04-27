package com.dsaviz.algorithms.tree;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class TreeTraversals extends Algorithm {

    public TreeTraversals() {
        this.name = "Tree Traversals (In/Pre/Post)";
        this.description = "Demonstrates Pre-order, In-order, and Post-order traversal "
                + "on a Binary Tree. Pre: Root-L-R, In: L-Root-R, Post: L-R-Root.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(h)";
        this.category = AlgorithmCategory.TREE;
    }

    private class TreeNode {
        int id, value, x, y;
        TreeNode left, right;
        TreeNode(int id, int value, int x, int y) {
            this.id = id; this.value = value; this.x = x; this.y = y;
        }
    }
    
    private TreeNode root;
    private int nextId = 0;
    
    @Override
    public void generateStructure(int size) {
        graphData.clear();
        nextId = 0;
        int n = Math.min(size, 15);
        int[] vals = new int[n];
        for (int i=0; i<n; i++) vals[i] = (int)(Math.random() * 99) + 1;
        
        // Build a balanced-ish CBT array layout
        root = buildTree(vals, 0, 0, 400, 80, 200);
        buildGraphData(root);
    }

    private TreeNode buildTree(int[] vals, int idx, int level, int x, int y, int xOffset) {
        if (idx >= vals.length) return null;
        TreeNode node = new TreeNode(nextId++, vals[idx], x, y);
        node.left = buildTree(vals, 2 * idx + 1, level + 1, x - xOffset, y + 80, xOffset / 2);
        node.right = buildTree(vals, 2 * idx + 2, level + 1, x + xOffset, y + 80, xOffset / 2);
        return node;
    }
    
    private void buildGraphData(TreeNode node) {
        if (node == null) return;
        graphData.addNode(new GraphNode(node.id, node.x, node.y, String.valueOf(node.value)));
        if (node.left != null) {
            graphData.addEdge(new GraphEdge(node.id, node.left.id, "", true));
            buildGraphData(node.left);
        }
        if (node.right != null) {
            graphData.addEdge(new GraphEdge(node.id, node.right.id, "", true));
            buildGraphData(node.right);
        }
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = graphData.getNodes().size();
        if (n == 0) return;

        int[] state = new int[nextId]; // 0=unvisited, etc

        // We will just do In-Order for simplicity of the demo, or all 3. Let's do In-order.
        step(state.clone(), -1, -1, "Starting In-Order Traversal (Left - Root - Right)...", callback);
        
        inorder(root, state, callback);
        
        step(state.clone(), -1, -1, "In-Order Complete. Now Pre-Order (Root - Left - Right)...", callback);
        state = new int[nextId];
        preorder(root, state, callback);
        
        step(state.clone(), -1, -1, "Pre-Order Complete. Now Post-Order (Left - Right - Root)...", callback);
        state = new int[nextId];
        postorder(root, state, callback);

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private void inorder(TreeNode node, int[] state, AlgorithmCallback callback) {
        if (node == null || cancelled) return;
        
        state[node.id] = 1; // Visited but not processed
        step(state.clone(), node.id, -1, "Going left from " + node.value, callback);
        
        inorder(node.left, state, callback);
        if (cancelled) return;
        
        state[node.id] = 2; // Active
        steps++;
        step(state.clone(), node.id, -1, "Processing node " + node.value, callback);
        
        state[node.id] = 3; // Done
        
        step(state.clone(), node.id, -1, "Going right from " + node.value, callback);
        inorder(node.right, state, callback);
    }
    
    private void preorder(TreeNode node, int[] state, AlgorithmCallback callback) {
        if (node == null || cancelled) return;
        
        state[node.id] = 2; // Active
        steps++;
        step(state.clone(), node.id, -1, "Processing node " + node.value, callback);
        state[node.id] = 3; // Done
        
        step(state.clone(), node.id, -1, "Going left from " + node.value, callback);
        preorder(node.left, state, callback);
        if (cancelled) return;
        
        step(state.clone(), node.id, -1, "Going right from " + node.value, callback);
        preorder(node.right, state, callback);
    }

    private void postorder(TreeNode node, int[] state, AlgorithmCallback callback) {
        if (node == null || cancelled) return;
        
        state[node.id] = 1; 
        step(state.clone(), node.id, -1, "Going left from " + node.value, callback);
        postorder(node.left, state, callback);
        if (cancelled) return;
        
        step(state.clone(), node.id, -1, "Going right from " + node.value, callback);
        postorder(node.right, state, callback);
        if (cancelled) return;
        
        state[node.id] = 2; // Active
        steps++;
        step(state.clone(), node.id, -1, "Processing node " + node.value, callback);
        state[node.id] = 3; // Done
    }
}
