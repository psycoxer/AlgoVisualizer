package com.dsaviz.core;

import javax.swing.SwingUtilities;

/**
 * Abstract base class for all visualizable algorithms.
 * Provides threading support (pause/resume/cancel) and
 * a step() helper that emits snapshots to the UI callback.
 */
public abstract class Algorithm {

    protected String name;
    protected String description;
    protected String timeComplexity;
    protected String spaceComplexity;
    protected AlgorithmCategory category;

    // --- Threading control ---
    protected volatile boolean cancelled = false;
    protected volatile boolean paused = false;
    protected volatile boolean stepOnce = false;
    protected int delay = 50; // milliseconds between steps
    protected final Object pauseLock = new Object();

    // --- Counters ---
    protected int comparisons = 0;
    protected int swaps = 0;
    protected int steps = 0;

    /**
     * Execute the algorithm on the given data, reporting each
     * visual step through the callback.
     */
    public abstract void execute(int[] data, AlgorithmCallback callback);

    // -------------------------------------------------------
    //  Step helper — clones data and pushes snapshot to EDT
    // -------------------------------------------------------
    protected void step(int[] data, int indexA, int indexB, String action, AlgorithmCallback callback) {
        try {
            checkPauseAndCancel();
            int[] snapshot = data.clone();
            SwingUtilities.invokeLater(() -> callback.onStep(snapshot, indexA, indexB, action));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cancelled = true;
        }
    }

    // -------------------------------------------------------
    //  Pause / resume / cancel
    // -------------------------------------------------------
    protected void checkPauseAndCancel() throws InterruptedException {
        if (cancelled) throw new InterruptedException("Cancelled");
        synchronized (pauseLock) {
            if (stepOnce) {
                stepOnce = false;
                paused = true;
            }
            while (paused && !cancelled) {
                pauseLock.wait();
            }
        }
        if (cancelled) throw new InterruptedException("Cancelled");
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public void stepForward() {
        stepOnce = true;
        resume();
    }

    public void cancel() {
        cancelled = true;
        resume(); // unblock if it was paused
    }

    public void reset() {
        paused = false;
        cancelled = false;
        comparisons = 0;
        swaps = 0;
        steps = 0;
    }

    // -------------------------------------------------------
    //  Getters / setters
    // -------------------------------------------------------
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; }
    public AlgorithmCategory getCategory() { return category; }
    
    // --- Graph Data ---
    protected GraphData graphData = new GraphData();
    public GraphData getGraphData() { return graphData; }
    public void setGraphData(GraphData graphData) { this.graphData = graphData; }
    
    /** Override to build tree/graph structures before start. */
    public void generateStructure(int size) {}

    public int getDelay() { return delay; }
    public void setDelay(int delay) { this.delay = Math.max(1, delay); }

    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }
    public int getSteps() { return steps; }

    public boolean isPaused() { return paused; }
    public boolean isCancelled() { return cancelled; }

    @Override
    public String toString() { return name; }
}
