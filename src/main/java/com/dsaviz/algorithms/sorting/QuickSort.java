package com.dsaviz.algorithms.sorting;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class QuickSort extends Algorithm {

    public QuickSort() {
        this.name = "Quick Sort";
        this.description = "A divide-and-conquer algorithm that selects a 'pivot' element "
                + "and partitions the array around it — elements less than the pivot go "
                + "left, greater go right — then recursively sorts the sub-arrays.";
        this.timeComplexity = "O(n log n) avg / O(n²) worst";
        this.spaceComplexity = "O(log n)";
        this.category = AlgorithmCategory.SORTING;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        quickSort(data, 0, data.length - 1, callback);
        long elapsed = System.currentTimeMillis() - startTime;

        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private void quickSort(int[] data, int low, int high, AlgorithmCallback callback) {
        if (low < high && !cancelled) {
            int pi = partition(data, low, high, callback);
            quickSort(data, low, pi - 1, callback);
            quickSort(data, pi + 1, high, callback);
        }
    }

    private int partition(int[] data, int low, int high, AlgorithmCallback callback) {
        int pivot = data[high];
        int i = low - 1;

        step(data, high, -1, "Pivot = " + pivot, callback);

        for (int j = low; j < high && !cancelled; j++) {
            comparisons++;
            step(data, j, high, "Comparing with pivot", callback);

            if (data[j] < pivot) {
                i++;
                int temp = data[i];
                data[i] = data[j];
                data[j] = temp;
                swaps++;
                step(data, i, j, "Swapped", callback);
            }
        }

        // Place pivot in its correct position
        int temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;
        swaps++;
        if (!cancelled) {
            step(data, i + 1, high, "Pivot placed at index " + (i + 1), callback);
        }

        return i + 1;
    }
}
