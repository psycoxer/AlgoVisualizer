package com.dsaviz.experiment;

/**
 * POJO representing a saved experiment — captures the full state
 * of an algorithm run for serialization to/from JSON files.
 */
public class Experiment {
    private String algorithmName;
    private String algorithmCategory;
    private int[] inputArray;
    private int[] resultArray;
    private int comparisons;
    private int swaps;
    private int steps;
    private long elapsedTimeMs;
    private String timestamp;
    private String notes;
    private int searchTarget;   // -1 if not a search
    private int searchResult;   // found index, -1 if not found

    public Experiment() {
        this.searchTarget = -1;
        this.searchResult = -1;
    }

    // --- Getters ---
    public String getAlgorithmName() { return algorithmName; }
    public String getAlgorithmCategory() { return algorithmCategory; }
    public int[] getInputArray() { return inputArray; }
    public int[] getResultArray() { return resultArray; }
    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }
    public int getSteps() { return steps; }
    public long getElapsedTimeMs() { return elapsedTimeMs; }
    public String getTimestamp() { return timestamp; }
    public String getNotes() { return notes; }
    public int getSearchTarget() { return searchTarget; }
    public int getSearchResult() { return searchResult; }

    // --- Setters ---
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }
    public void setAlgorithmCategory(String algorithmCategory) { this.algorithmCategory = algorithmCategory; }
    public void setInputArray(int[] inputArray) { this.inputArray = inputArray; }
    public void setResultArray(int[] resultArray) { this.resultArray = resultArray; }
    public void setComparisons(int comparisons) { this.comparisons = comparisons; }
    public void setSwaps(int swaps) { this.swaps = swaps; }
    public void setSteps(int steps) { this.steps = steps; }
    public void setElapsedTimeMs(long elapsedTimeMs) { this.elapsedTimeMs = elapsedTimeMs; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setSearchTarget(int searchTarget) { this.searchTarget = searchTarget; }
    public void setSearchResult(int searchResult) { this.searchResult = searchResult; }

    @Override
    public String toString() {
        return algorithmName + " — " + timestamp;
    }
}
