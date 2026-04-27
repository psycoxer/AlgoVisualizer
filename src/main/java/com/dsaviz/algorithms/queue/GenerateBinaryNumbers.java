package com.dsaviz.algorithms.queue;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.LinkedList;

/**
 * Generates binary number representations from 1 to N
 * using a queue (classic BFS-style generation).
 */
public class GenerateBinaryNumbers extends Algorithm {

    public GenerateBinaryNumbers() {
        this.name = "Binary Numbers (Queue)";
        this.description = "Generates binary representations of numbers 1 to N using a queue. "
                + "Start with \"1\", then repeatedly dequeue the front and enqueue "
                + "front+\"0\" and front+\"1\". A classic queue application.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.QUEUE;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = Math.min(data.length, 20); // cap to avoid overflow

        LinkedList<String> queue = new LinkedList<>();
        queue.add("1");

        step(toIntArray(queue), 0, -1, "Start: enqueue \"1\"", callback);

        for (int i = 0; i < n && !cancelled; i++) {
            String front = queue.removeFirst();
            comparisons++;
            int decimal = Integer.parseInt(front, 2);

            step(toIntArray(queue), -1, -1,
                    "Dequeue: " + front + "  (decimal " + decimal + ")", callback);

            String left = front + "0";
            String right = front + "1";
            queue.addLast(left);
            queue.addLast(right);
            swaps += 2;

            int[] state = toIntArray(queue);
            step(state, state.length - 2, state.length - 1,
                    "Enqueue: " + left + " and " + right, callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    /** Convert queue of binary strings to int array for display. */
    private int[] toIntArray(LinkedList<String> queue) {
        return queue.stream()
                .mapToInt(s -> {
                    try { return Integer.parseInt(s); }
                    catch (NumberFormatException e) { return 0; }
                })
                .toArray();
    }
}
