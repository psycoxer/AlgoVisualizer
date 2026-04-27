package com.dsaviz.algorithms.searching;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.Arrays;

public class BinarySearch extends Algorithm {

    private int target;
    private int foundIndex = -1;

    public BinarySearch() {
        this.name = "Binary Search";
        this.description = "Finds the position of a target value within a sorted array by "
                + "repeatedly dividing the search interval in half. The array is automatically "
                + "sorted before searching.";
        this.timeComplexity = "O(log n)";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.SEARCHING;
    }

    public void setTarget(int target) { this.target = target; }
    public int getTarget() { return target; }
    public int getFoundIndex() { return foundIndex; }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        // Binary search requires sorted data
        Arrays.sort(data);
        step(data, -1, -1, "Array sorted for binary search", callback);

        long startTime = System.currentTimeMillis();
        foundIndex = -1;

        int low = 0, high = data.length - 1;

        while (low <= high && !cancelled) {
            int mid = low + (high - low) / 2;
            comparisons++;
            step(data, mid, -1,
                    "Mid=" + mid + " value=" + data[mid] + " | range [" + low + ".." + high + "]",
                    callback);

            if (data[mid] == target) {
                foundIndex = mid;
                step(data, mid, -1, "✓ Found " + target + " at index " + mid, callback);
                break;
            } else if (data[mid] < target) {
                step(data, mid, high, "Target > mid → search right half", callback);
                low = mid + 1;
            } else {
                step(data, low, mid, "Target < mid → search left half", callback);
                high = mid - 1;
            }
        }

        if (foundIndex == -1 && !cancelled) {
            step(data, -1, -1, "✗ " + target + " not found in the array", callback);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            final int idx = foundIndex;
            SwingUtilities.invokeLater(() -> {
                AlgorithmResult result = new AlgorithmResult(name, comparisons, swaps, steps, elapsed);
                result.setSearchTarget(target);
                result.setSearchResult(idx);
                callback.onComplete(result);
            });
        }
    }
}
