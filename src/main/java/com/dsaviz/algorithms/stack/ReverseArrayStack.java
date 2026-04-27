package com.dsaviz.algorithms.stack;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reverses an array by pushing all elements onto a stack,
 * then popping them into a new array.
 */
public class ReverseArrayStack extends Algorithm {

    public ReverseArrayStack() {
        this.name = "Reverse Array (Stack)";
        this.description = "Reverses an array using a stack. All elements are pushed "
                + "onto the stack, then popped off — since a stack is LIFO, "
                + "the elements come out in reverse order.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.STACK;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        List<Integer> stack = new ArrayList<>();
        int[] original = data.clone();

        step(new int[0], -1, -1,
                "Original: " + Arrays.toString(original), callback);

        // ── Push all ─────────────────────────────────
        for (int val : original) {
            if (cancelled) return;
            stack.add(val);
            swaps++;
            step(toArray(stack), stack.size() - 1, -1,
                    "Push: " + val, callback);
        }

        step(toArray(stack), stack.size() - 1, -1,
                "All elements pushed — now popping to reverse", callback);

        // ── Pop all → reversed ───────────────────────
        int[] reversed = new int[original.length];
        int idx = 0;
        while (!stack.isEmpty() && !cancelled) {
            int val = stack.remove(stack.size() - 1);
            reversed[idx++] = val;
            comparisons++;
            int hi = stack.isEmpty() ? -1 : stack.size() - 1;
            step(toArray(stack), hi, -1,
                    "Pop: " + val + " → reversed[" + (idx - 1) + "]", callback);
        }

        if (!cancelled) {
            step(new int[0], -1, -1,
                    "Reversed: " + Arrays.toString(reversed), callback);
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
