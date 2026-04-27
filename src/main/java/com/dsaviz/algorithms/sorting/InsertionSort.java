package com.dsaviz.algorithms.sorting;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class InsertionSort extends Algorithm {

    public InsertionSort() {
        this.name = "Insertion Sort";
        this.description = "Builds the final sorted array one item at a time. "
                + "It picks the next element and inserts it into the correct position "
                + "among the previously sorted elements.";
        this.timeComplexity = "O(n²)";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.SORTING;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        int n = data.length;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i < n && !cancelled; i++) {
            int key = data[i];
            int j = i - 1;

            step(data, i, -1, "Selecting key = " + key, callback);

            while (j >= 0 && !cancelled) {
                comparisons++;
                step(data, j, j + 1, "Comparing", callback);

                if (data[j] > key) {
                    data[j + 1] = data[j];
                    swaps++;
                    step(data, j, j + 1, "Shifting right", callback);
                    j--;
                } else {
                    break;
                }
            }
            data[j + 1] = key;
            if (!cancelled) {
                step(data, j + 1, -1, "Inserted key at index " + (j + 1), callback);
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
