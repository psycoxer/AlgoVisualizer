package com.dsaviz.algorithms.sorting;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class MergeSort extends Algorithm {

    public MergeSort() {
        this.name = "Merge Sort";
        this.description = "A divide-and-conquer algorithm that splits the array in half, "
                + "recursively sorts each half, then merges the sorted halves. "
                + "Guarantees O(n log n) time complexity.";
        this.timeComplexity = "O(n log n)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.SORTING;
    }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        mergeSort(data, 0, data.length - 1, callback);
        long elapsed = System.currentTimeMillis() - startTime;

        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }

    private void mergeSort(int[] data, int left, int right, AlgorithmCallback callback) {
        if (left < right && !cancelled) {
            int mid = left + (right - left) / 2;
            mergeSort(data, left, mid, callback);
            mergeSort(data, mid + 1, right, callback);
            merge(data, left, mid, right, callback);
        }
    }

    private void merge(int[] data, int left, int mid, int right, AlgorithmCallback callback) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right && !cancelled) {
            comparisons++;
            step(data, i, j, "Comparing", callback);

            if (data[i] <= data[j]) {
                temp[k++] = data[i++];
            } else {
                temp[k++] = data[j++];
            }
        }

        while (i <= mid) temp[k++] = data[i++];
        while (j <= right) temp[k++] = data[j++];

        for (int m = 0; m < temp.length && !cancelled; m++) {
            data[left + m] = temp[m];
            swaps++;
            step(data, left + m, -1, "Merging", callback);
        }
    }
}
