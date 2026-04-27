package com.dsaviz.core;

/**
 * Callback interface for algorithm step-by-step visualization.
 * Implemented by the visualization panel to animate each operation.
 */
public interface AlgorithmCallback {

    /**
     * Called on each algorithmic step (compare, swap, shift, etc.)
     *
     * @param currentState snapshot of the array at this step
     * @param indexA       primary highlighted index (e.g., element being compared)
     * @param indexB       secondary highlighted index (-1 if unused)
     * @param action       human-readable description of the action
     */
    void onStep(int[] currentState, int indexA, int indexB, String action);

    /**
     * Called when the algorithm finishes execution.
     *
     * @param result summary statistics of the run
     */
    void onComplete(AlgorithmResult result);
}
