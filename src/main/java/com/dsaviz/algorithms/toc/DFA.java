package com.dsaviz.algorithms.toc;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;

public class DFA extends Algorithm {

    private String inputString = "";

    public DFA() {
        this.name = "Deterministic Finite Automaton (DFA)";
        this.description = "A theoretical machine that accepts or rejects strings. "
                + "This example DFA accepts any string ending in 'ab'. "
                + "Alphabet is {a, b}.";
        this.timeComplexity = "O(n) where n is string length";
        this.spaceComplexity = "O(1)";
        this.category = AlgorithmCategory.TOC;
    }

    public void setInputString(String input) {
        this.inputString = input;
    }

    @Override
    public void generateStructure(int size) {
        graphData.clear();
        
        // Let's create a DFA for "(a|b)*ab" -> accepts strings ending in 'ab'
        // q0 (start)
        // q1 (seen 'a')
        // q2 (seen 'ab', accepting)

        GraphNode q0 = new GraphNode(0, 200, 250, "q0");
        q0.setStartState(true);
        graphData.addNode(q0);

        GraphNode q1 = new GraphNode(1, 400, 250, "q1");
        graphData.addNode(q1);

        GraphNode q2 = new GraphNode(2, 600, 250, "q2");
        q2.setFinalState(true);
        graphData.addNode(q2);

        // Edges/Transitions
        // Use directed edges with labels
        graphData.addEdge(new GraphEdge(0, 0, "b", true));
        graphData.addEdge(new GraphEdge(0, 1, "a", true));
        
        graphData.addEdge(new GraphEdge(1, 1, "a", true));
        graphData.addEdge(new GraphEdge(1, 2, "b", true));
        
        graphData.addEdge(new GraphEdge(2, 1, "a", true));
        graphData.addEdge(new GraphEdge(2, 0, "b", true));
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = graphData.getNodes().size();
        if (n == 0) return;

        int[] state = new int[n]; // 0=inactive, 2=active

        int currentState = 0; // q0 is start state
        state[currentState] = 2;
        steps++;
        
        step(state.clone(), currentState, -1, "Start at state q0. Tape: " + inputString, callback);

        for (int i = 0; i < inputString.length(); i++) {
            if (cancelled) return;
            
            char c = inputString.charAt(i);
            String remaining = inputString.substring(i);
            
            // Find transition matching character
            int nextState = -1;
            for (GraphEdge edge : graphData.getEdges()) {
                if (edge.getSourceId() == currentState && edge.getLabel().contains(String.valueOf(c))) {
                    nextState = edge.getTargetId();
                    step(state.clone(), currentState, nextState, "Read '" + c + "'. Following transition to q" + nextState, callback);
                    break;
                }
            }

            if (nextState == -1) {
                // Should not happen for a complete DFA, but handle it
                step(state.clone(), currentState, -1, "No transition for '" + c + "'. REJECTED.", callback);
                return; // Early fail
            }
            
            state[currentState] = 0;
            currentState = nextState;
            state[currentState] = 2; // Active
            steps++;
            comparisons++;
            
            step(state.clone(), currentState, -1, "Moved to q" + currentState + ". Tape: " + remaining.substring(1), callback);
        }

        boolean accepted = graphData.getNodes().get(currentState).isFinalState();
        if (accepted) {
            state[currentState] = 3; // Done/Valid
        }
        
        String result = accepted ? "ACCEPTED!" : "REJECTED!";
        step(state.clone(), currentState, -1, "Input string completely read. Result: " + result, callback);

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
