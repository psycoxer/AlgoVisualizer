package com.dsaviz.core;

public enum AlgorithmCategory {
    SORTING("Sorting"),
    SEARCHING("Searching"),
    STACK("Stack"),
    QUEUE("Queue"),
    TREE("Tree"),
    GRAPH("Graph"),
    TOC("ToC");

    private final String displayName;

    AlgorithmCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
