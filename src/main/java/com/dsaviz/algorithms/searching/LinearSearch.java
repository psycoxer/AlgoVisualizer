package com.dsaviz.algorithms.searching;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class LinearSearch extends Algorithm {

    private int target;
    private int foundIndex = -1;

    public LinearSearch() {
        this.name = "Linear Search";
        this.description = "Sequentially checks each element of the list until a match "
                + "is found or the whole list has been searched. Works on unsorted arrays.";
        this.timeComplexity = "O(n)";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.SEARCHING;
    }

    public void setTarget(int target) { this.target = target; }
    public int getTarget() { return target; }
    public int getFoundIndex() { return foundIndex; }

    @Override
    public void execute(int[] data, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        foundIndex = -1;

        for (int i = 0; i < data.length && !cancelled; i++) {
            comparisons++;
            step(data, i, -1, "Checking index " + i + " (value=" + data[i] + ")", callback);

            if (data[i] == target) {
                foundIndex = i;
                step(data, i, -1, "✓ Found " + target + " at index " + i, callback);
                break;
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
