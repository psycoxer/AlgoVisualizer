package com.dsaviz.algorithms.stack;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Push and Pop operations on a stack (LIFO).
 * Pushes all values one-by-one, peeks, then pops all.
 */
public class StackPushPop extends Algorithm {

    public StackPushPop() {
        this.name = "Stack Push / Pop";
        this.description = "Demonstrates Last-In-First-Out (LIFO) behavior. "
                + "Values are pushed onto the stack one by one, then popped "
                + "off in reverse order.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.STACK;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        List<Integer> stack = new ArrayList<>();

        // ── Phase 1: Push ────────────────────────────
        for (int val : data) {
            if (cancelled) return;
            stack.add(val);
            swaps++; // reuse as "pushes"
            step(toArray(stack), stack.size() - 1, -1,
                    "Push: " + val + "  │  size = " + stack.size(), callback);
        }

        // ── Phase 2: Peek ────────────────────────────
        if (!stack.isEmpty() && !cancelled) {
            step(toArray(stack), stack.size() - 1, -1,
                    "Peek → " + stack.get(stack.size() - 1) + "  (top element)", callback);
        }

        // ── Phase 3: Pop ─────────────────────────────
        while (!stack.isEmpty() && !cancelled) {
            int val = stack.remove(stack.size() - 1);
            comparisons++; // reuse as "pops"
            int[] state = toArray(stack);
            int hi = stack.isEmpty() ? -1 : stack.size() - 1;
            step(state, hi, -1,
                    "Pop: " + val + "  │  remaining = " + stack.size(), callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private int[] toArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
