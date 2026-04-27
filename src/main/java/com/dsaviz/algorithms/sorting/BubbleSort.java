package com.dsaviz.algorithms.sorting;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class BubbleSort extends Algorithm {

    public BubbleSort() {
        this.name = "Bubble Sort";
        this.description = "Repeatedly steps through the list, compares adjacent elements "
                + "and swaps them if they are in the wrong order. The pass through the list "
                + "is repeated until the list is sorted.";
        this.timeComplexity = "O(n²)";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.SORTING;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        int n = data.length;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1 && !cancelled; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1 && !cancelled; j++) {
                comparisons++;
                step(data, j, j + 1, "Comparing", callback);

                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                    swaps++;
                    step(data, j, j + 1, "Swapped", callback);
                    swapped = true;
                }
            }
            if (!swapped) break; // optimisation: already sorted
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
