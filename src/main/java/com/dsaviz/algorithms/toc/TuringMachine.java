package com.dsaviz.algorithms.toc;

import com.dsaviz.core.*;
import javax.swing.SwingUtilities;
import java.util.Arrays;

public class TuringMachine extends Algorithm {

    private String inputString = "";
    private char[] tape;
    private int headPos;

    public TuringMachine() {
        this.name = "Turing Machine (A^n B^n)";
        this.description = "A Turing Machine recognizing the context-free language A^n B^n. "
                + "It matches a's and b's by crossing them out as X's and Y's, moving back and forth.";
        this.timeComplexity = "O(n^2)";
        this.spaceComplexity = "O(n)";
        this.category = AlgorithmCategory.TOC;
    }

    public void setInputString(String input) {
        this.inputString = input;
    }

    public char[] getTape() {
        return tape;
    }

    public int getHeadPos() {
        return headPos;
    }

    @Override
    public void generateStructure(int size) {
        graphData.clear();

        // q0 (start)
        GraphNode q0 = new GraphNode(0, 150, 250, "q0");
        q0.setStartState(true);
        graphData.addNode(q0);

        // q1 (seek Right)
        GraphNode q1 = new GraphNode(1, 350, 150, "q1");
        graphData.addNode(q1);

        // q2 (seek Left)
        GraphNode q2 = new GraphNode(2, 350, 350, "q2");
        graphData.addNode(q2);

        // q3 (verify end)
        GraphNode q3 = new GraphNode(3, 550, 250, "q3");
        graphData.addNode(q3);

        // q4 (accept)
        GraphNode q4 = new GraphNode(4, 750, 250, "qA");
        q4.setFinalState(true);
        graphData.addNode(q4);

        // Edges: source, target, label (Read -> Write, Move), directed=true
        // From q0
        graphData.addEdge(new GraphEdge(0, 1, "a->X, R", true));
        graphData.addEdge(new GraphEdge(0, 3, "Y->Y, R", true));
        graphData.addEdge(new GraphEdge(0, 4, "_->_, R", true)); // Accept empty string

        // From q1
        graphData.addEdge(new GraphEdge(1, 1, "a->a, R | Y->Y, R", true));
        graphData.addEdge(new GraphEdge(1, 2, "b->Y, L", true));

        // From q2
        graphData.addEdge(new GraphEdge(2, 2, "a->a, L | Y->Y, L", true));
        graphData.addEdge(new GraphEdge(2, 0, "X->X, R", true));

        // From q3
        graphData.addEdge(new GraphEdge(3, 3, "Y->Y, R", true));
        graphData.addEdge(new GraphEdge(3, 4, "_->_, R", true));
    }

    @Override
    public void execute(int[] inData, AlgorithmCallback callback) {
        long startTime = System.currentTimeMillis();
        int n = graphData.getNodes().size();
        if (n == 0) return;

        // Initialize Tape (size 100, head centered around 50)
        tape = new char[100];
        Arrays.fill(tape, '_');
        headPos = 50 - inputString.length() / 2;
        
        for (int i = 0; i < inputString.length(); i++) {
            tape[headPos + i] = inputString.charAt(i);
        }

        int[] state = new int[n]; // 0=inactive, 2=active
        int currentState = 0; // q0
        
        state[currentState] = 2;
        step(state.clone(), currentState, -1, "Start at q0", callback);

        boolean accepted = false;

        while (true) {
            if (cancelled) return;
            
            // Expand tape if needed
            if (headPos < 0) {
                char[] newTape = new char[tape.length + 50];
                Arrays.fill(newTape, '_');
                System.arraycopy(tape, 0, newTape, 50, tape.length);
                tape = newTape;
                headPos += 50;
            } else if (headPos >= tape.length) {
                char[] newTape = new char[tape.length + 50];
                Arrays.fill(newTape, '_');
                System.arraycopy(tape, 0, newTape, 0, tape.length);
                tape = newTape;
            }

            char read = tape[headPos];
            int nextState = -1;
            char write = read;
            int move = 0; // -1=L, 1=R

            // Transition logic based on currentState and read char
            if (currentState == 0) {
                if (read == 'a') { nextState = 1; write = 'X'; move = 1; }
                else if (read == 'Y') { nextState = 3; write = 'Y'; move = 1; }
                else if (read == '_') { nextState = 4; write = '_'; move = 1; }
            } else if (currentState == 1) {
                if (read == 'a') { nextState = 1; write = 'a'; move = 1; }
                else if (read == 'Y') { nextState = 1; write = 'Y'; move = 1; }
                else if (read == 'b') { nextState = 2; write = 'Y'; move = -1; }
            } else if (currentState == 2) {
                if (read == 'a') { nextState = 2; write = 'a'; move = -1; }
                else if (read == 'Y') { nextState = 2; write = 'Y'; move = -1; }
                else if (read == 'X') { nextState = 0; write = 'X'; move = 1; }
            } else if (currentState == 3) {
                if (read == 'Y') { nextState = 3; write = 'Y'; move = 1; }
                else if (read == '_') { nextState = 4; write = '_'; move = 1; }
            }

            if (nextState == -1) {
                // Reject
                step(state.clone(), currentState, -1, "No transition for '" + read + "'. REJECTED.", callback);
                break;
            }

            // Apply transition
            tape[headPos] = write;
            headPos += move;
            
            state[currentState] = 0;
            currentState = nextState;
            state[currentState] = 2;
            
            steps++;
            comparisons++;
            
            String moveDir = move == 1 ? "R" : "L";
            step(state.clone(), currentState, -1, "Read '" + read + "', Wrote '" + write + "', Moved " + moveDir + " -> q" + currentState, callback);

            // q4 is the only final state
            if (currentState == 4) {
                accepted = true;
                state[currentState] = 3;
                break;
            }
        }

        String result = accepted ? "ACCEPTED!" : "REJECTED!";
        step(state.clone(), currentState, -1, "Algorithm complete. " + result, callback);

        long elapsed = System.currentTimeMillis() - startTime;
        if (!cancelled) {
            SwingUtilities.invokeLater(() ->
                    callback.onComplete(new AlgorithmResult(name, comparisons, swaps, steps, elapsed)));
        }
    }
}
