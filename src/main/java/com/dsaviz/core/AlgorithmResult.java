package com.dsaviz.core;

/**
 * Stores the result statistics of an algorithm execution.
 */
public class AlgorithmResult {
    private String algorithmName;
    private int comparisons;
    private int swaps;
    private int steps;
    private long elapsedTimeMs;
    private int searchTarget = -1;
    private int searchResult = -1; // index where found, -1 if not found

    public AlgorithmResult() {}

    public AlgorithmResult(String algorithmName, int comparisons, int swaps, int steps, long elapsedTimeMs) {
        this.algorithmName = algorithmName;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.steps = steps;
        this.elapsedTimeMs = elapsedTimeMs;
    }

    // --- Getters ---
    public String getAlgorithmName() { return algorithmName; }
    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }
    public int getSteps() { return steps; }
    public long getElapsedTimeMs() { return elapsedTimeMs; }
    public int getSearchTarget() { return searchTarget; }
    public int getSearchResult() { return searchResult; }

    // --- Setters ---
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }
    public void setComparisons(int comparisons) { this.comparisons = comparisons; }
    public void setSwaps(int swaps) { this.swaps = swaps; }
    public void setSteps(int steps) { this.steps = steps; }
    public void setElapsedTimeMs(long elapsedTimeMs) { this.elapsedTimeMs = elapsedTimeMs; }
    public void setSearchTarget(int searchTarget) { this.searchTarget = searchTarget; }
    public void setSearchResult(int searchResult) { this.searchResult = searchResult; }
}
