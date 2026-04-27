package com.dsaviz.algorithms.sorting;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class SelectionSort extends Algorithm {

    public SelectionSort() {
        this.name = "Selection Sort";
        this.description = "Divides the input list into a sorted and an unsorted region. "
                + "It repeatedly selects the smallest element from the unsorted region "
                + "and moves it to the end of the sorted region.";
        this.timeComplexity = "O(n²)";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.SORTING;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        int n = data.length;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1 && !cancelled; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n && !cancelled; j++) {
                comparisons++;
                step(data, minIdx, j, "Comparing", callback);

                if (data[j] < data[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i && !cancelled) {
                int temp = data[i];
                data[i] = data[minIdx];
                data[minIdx] = temp;
                swaps++;
                step(data, i, minIdx, "Swapped", callback);
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
