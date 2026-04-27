package com.dsaviz.algorithms.queue;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.LinkedList;

/**
 * Demonstrates Enqueue and Dequeue operations on a queue (FIFO).
 * Enqueues all values one-by-one, peeks, then dequeues all.
 */
public class QueueEnqueueDequeue extends Algorithm {

    public QueueEnqueueDequeue() {
        this.name = "Queue Enqueue / Dequeue";
        this.description = "Demonstrates First-In-First-Out (FIFO) behavior. "
                + "Values are enqueued at the rear one by one, then dequeued "
                + "from the front in the same order.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.QUEUE;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        LinkedList<Integer> queue = new LinkedList<>();

        // ── Phase 1: Enqueue ─────────────────────────
        for (int val : data) {
            if (cancelled) return;
            queue.addLast(val);
            swaps++; // reuse as "enqueues"
            step(toArray(queue), queue.size() - 1, -1,
                    "Enqueue: " + val + "  │  size = " + queue.size(), callback);
        }

        // ── Phase 2: Peek ────────────────────────────
        if (!queue.isEmpty() && !cancelled) {
            step(toArray(queue), 0, -1,
                    "Peek → " + queue.peekFirst() + "  (front element)", callback);
        }

        // ── Phase 3: Dequeue ─────────────────────────
        while (!queue.isEmpty() && !cancelled) {
            int val = queue.removeFirst();
            comparisons++; // reuse as "dequeues"
            int[] state = toArray(queue);
            step(state, queue.isEmpty() ? -1 : 0, -1,
                    "Dequeue: " + val + "  │  remaining = " + queue.size(), callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private int[] toArray(LinkedList<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
