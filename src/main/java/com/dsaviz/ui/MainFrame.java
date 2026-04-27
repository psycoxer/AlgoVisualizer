package com.dsaviz.ui;

import com.dsaviz.algorithms.searching.BinarySearch;
import com.dsaviz.algorithms.searching.LinearSearch;
import com.dsaviz.algorithms.sorting.*;
import com.dsaviz.algorithms.stack.*;
import com.dsaviz.algorithms.queue.*;
import com.dsaviz.algorithms.graph.*;
import com.dsaviz.algorithms.tree.*;
import com.dsaviz.algorithms.toc.*;
import com.dsaviz.core.*;
import com.dsaviz.experiment.Experiment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main application window — wires together sidebar, visualization,
 * controls, info panel, and experiment save/load menus.
 */
public class MainFrame extends JFrame {

    // --- Sub-panels ---
    private final VisualizationPanel vizPanel;
    private final StackQueueVisualizationPanel sqVizPanel;
    private final GraphTreeVisualizationPanel gtVizPanel;
    private final ControlPanel controlPanel;
    private final SidebarPanel sidebarPanel;
    private final InfoPanel infoPanel;
    private CardLayout vizCardLayout;
    private JPanel vizContainer;

    // --- State ---
    private Algorithm currentAlgorithm;
    private int[] currentData;
    private int[] originalData;
    private Thread algorithmThread;
    private AlgorithmResult lastResult;
    private final List<Algorithm> algorithms;
    private final Random random = new Random();

    public MainFrame() {
        setTitle("DSA Algorithm Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1320, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // --- Initialise algorithms ---
        algorithms = new ArrayList<>();
        algorithms.add(new BubbleSort());
        algorithms.add(new SelectionSort());
        algorithms.add(new InsertionSort());
        algorithms.add(new MergeSort());
        algorithms.add(new QuickSort());
        algorithms.add(new LinearSearch());
        algorithms.add(new BinarySearch());
        algorithms.add(new StackPushPop());
        algorithms.add(new ReverseArrayStack());
        algorithms.add(new QueueEnqueueDequeue());
        algorithms.add(new GenerateBinaryNumbers());
        
        algorithms.add(new BSTInsertSearch());
        algorithms.add(new TreeTraversals());
        algorithms.add(new BFS());
        algorithms.add(new DFS());
        algorithms.add(new Dijkstra());
        
        algorithms.add(new DFA());
        algorithms.add(new TuringMachine());

        // --- Create panels ---
        vizPanel     = new VisualizationPanel();
        sqVizPanel   = new StackQueueVisualizationPanel();
        gtVizPanel   = new GraphTreeVisualizationPanel();
        controlPanel = new ControlPanel();
        infoPanel    = new InfoPanel();
        sidebarPanel = new SidebarPanel(algorithms, this::selectAlgorithm);

        // --- Layout ---
        buildLayout();
        buildMenuBar();
        wireControls();

        // --- Defaults ---
        selectAlgorithm(algorithms.get(0));
        generateArray();
    }

    // ==========================================================
    //  Layout
    // ==========================================================
    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(16, 20, 36));

        // LEFT: sidebar + info stacked vertically
        JPanel leftColumn = new JPanel(new BorderLayout());
        leftColumn.setPreferredSize(new Dimension(230, 0));
        leftColumn.add(sidebarPanel, BorderLayout.CENTER);
        JScrollPane infoScroll = new JScrollPane(infoPanel);
        infoScroll.setBorder(null);
        infoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroll.setPreferredSize(new Dimension(230, 320));
        leftColumn.add(infoScroll, BorderLayout.SOUTH);

        // Add a subtle separator line
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(new Color(50, 50, 70));

        // RIGHT: visualization (card layout) + controls
        vizCardLayout = new CardLayout();
        vizContainer = new JPanel(vizCardLayout);
        vizContainer.add(vizPanel, "BARS");
        vizContainer.add(sqVizPanel, "BLOCKS");
        vizContainer.add(gtVizPanel, "GRAPH_TREE");

        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setBackground(new Color(22, 27, 44));
        rightColumn.add(vizContainer, BorderLayout.CENTER);
        rightColumn.add(controlPanel, BorderLayout.SOUTH);

        root.add(leftColumn, BorderLayout.WEST);
        root.add(sep, BorderLayout.CENTER); // thin line
        root.add(rightColumn, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ==========================================================
    //  Menu bar  (File → Save / Load / Exit)
    // ==========================================================
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Experiment…");
        JMenuItem loadItem = new JMenuItem("Load Experiment…");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(e -> saveExperiment());
        loadItem.addActionListener(e -> loadExperiment());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "DSA Algorithm Visualizer v1.0\n"
                                + "A Java Mini-Project\n\n"
                                + "Visualise sorting, searching, stack & queue\n"
                                + "algorithms step-by-step with animations.",
                        "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // ==========================================================
    //  Wire control panel actions
    // ==========================================================
    private void wireControls() {
        controlPanel.getGenerateButton().addActionListener(e -> generateArray());
        controlPanel.getCustomInputButton().addActionListener(e -> promptCustomInput());
        controlPanel.getStartButton().addActionListener(e -> startAlgorithm());
        controlPanel.getStepButton().addActionListener(e -> stepAlgorithm());
        controlPanel.getPauseButton().addActionListener(e -> togglePause());
        controlPanel.getResetButton().addActionListener(e -> resetAlgorithm());
        controlPanel.getSpeedSlider().addChangeListener(e -> {
            if (currentAlgorithm != null) {
                currentAlgorithm.setDelay(controlPanel.getSpeedDelay());
            }
        });
    }

    // ==========================================================
    //  Algorithm selection
    // ==========================================================
    private void selectAlgorithm(Algorithm algo) {
        // Cancel any running algorithm
        if (algorithmThread != null && algorithmThread.isAlive()) {
            currentAlgorithm.cancel();
        }

        currentAlgorithm = algo;
        currentAlgorithm.setDelay(controlPanel.getSpeedDelay());
        controlPanel.setSearchMode(algo.getCategory() == AlgorithmCategory.SEARCHING || algo.getCategory() == AlgorithmCategory.TOC);
        if (algo.getCategory() == AlgorithmCategory.TOC) {
            controlPanel.setSearchLabel("Tape:");
        } else {
            controlPanel.setSearchLabel("Target:");
        }

        // Cap size slider for stack/queue so blocks stay readable
        if (isBarMode(algo)) {
            controlPanel.setSizeRange(10, 200);
        } else {
            controlPanel.setSizeRange(5, 40);
        }

        // Switch visualization panel
        if (isBarMode(algo)) {
            vizCardLayout.show(vizContainer, "BARS");
        } else if (isGraphTreeMode(algo)) {
            gtVizPanel.setAlgorithm(algo);
            vizCardLayout.show(vizContainer, "GRAPH_TREE");
        } else {
            sqVizPanel.setStackMode(algo.getCategory() == AlgorithmCategory.STACK);
            vizCardLayout.show(vizContainer, "BLOCKS");
        }

        infoPanel.setAlgorithmInfo(
                algo.getName(), algo.getDescription(),
                algo.getTimeComplexity(), algo.getSpaceComplexity());

        controlPanel.setIdleState();
        lastResult = null;

        // Re-generate the array for the new algorithm
        generateArray();
    }

    private boolean isBarMode(Algorithm algo) {
        return algo.getCategory() == AlgorithmCategory.SORTING
                || algo.getCategory() == AlgorithmCategory.SEARCHING;
    }

    private boolean isGraphTreeMode(Algorithm algo) {
        return algo.getCategory() == AlgorithmCategory.TREE
                || algo.getCategory() == AlgorithmCategory.GRAPH
                || algo.getCategory() == AlgorithmCategory.TOC;
    }

    // ==========================================================
    //  Array generation
    // ==========================================================
    private void generateArray() {
        int size = controlPanel.getArraySize();
        currentData = new int[size];
        for (int i = 0; i < size; i++) {
            currentData[i] = random.nextInt(size) + 1;
        }
        originalData = currentData.clone();

        if (isBarMode(currentAlgorithm)) {
            controlPanel.setSizeRange(10, 200);
            vizPanel.setData(currentData.clone());
        } else if (isGraphTreeMode(currentAlgorithm)) {
            controlPanel.setSizeRange(5, 20); // Cap sizes for clear trees/graphs
            size = controlPanel.getArraySize(); 
            currentAlgorithm.generateStructure(size);
            gtVizPanel.setData(currentData.clone());
            gtVizPanel.clearState();
        } else {
            controlPanel.setSizeRange(5, 40);
            sqVizPanel.setData(currentData.clone());
        }
        infoPanel.resetStats();
        infoPanel.setStatus("Ready", new Color(0, 210, 255));
    }

    // ==========================================================
    //  Custom Input Handling
    // ==========================================================
    private void promptCustomInput() {
        if (currentAlgorithm == null) return;
        boolean isGraph = currentAlgorithm.getCategory() == AlgorithmCategory.GRAPH;
        CustomInputDialog dialog = new CustomInputDialog(this, isGraph);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String text = dialog.getResultText();
            if (text == null || text.isEmpty()) return;

            if (isGraph) {
                parseCustomGraph(text);
                gtVizPanel.clearState();
            } else {
                parseCustomArray(text);
                if (isBarMode(currentAlgorithm)) vizPanel.setData(currentData.clone());
                else if (isGraphTreeMode(currentAlgorithm)) gtVizPanel.setData(currentData.clone());
                else sqVizPanel.setData(currentData.clone());
            }
            originalData = currentData.clone();
            infoPanel.resetStats();
            infoPanel.setStatus("Custom Data Ready", new Color(138, 43, 226));
        }
    }

    private void parseCustomArray(String text) {
        String[] parts = text.split("[,\\s]+");
        List<Integer> list = new ArrayList<>();
        for (String p : parts) {
            try { list.add(Integer.parseInt(p.trim())); } catch (NumberFormatException ignored) {}
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid numbers found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentData = list.stream().mapToInt(i -> i).toArray();
        if (currentAlgorithm.getCategory() == AlgorithmCategory.TREE) {
             currentAlgorithm.generateStructure(currentData.length);
        }
    }

    private void parseCustomGraph(String text) {
        GraphData gd = new GraphData();
        String[] lines = text.split("\n");
        int nextId = 0;
        java.util.Map<String, Integer> nodeMap = new java.util.HashMap<>();
        
        for (String line : lines) {
            String[] parts = line.split("[,\\s]+");
            if (parts.length < 2) continue;
            String src = parts[0].trim();
            String tgt = parts[1].trim();
            String weight = parts.length > 2 ? parts[2].trim() : "1";
            
            if (!nodeMap.containsKey(src)) nodeMap.put(src, nextId++);
            if (!nodeMap.containsKey(tgt)) nodeMap.put(tgt, nextId++);
            
            gd.addEdge(new GraphEdge(nodeMap.get(src), nodeMap.get(tgt), weight, false));
        }
        
        int n = nodeMap.size();
        if (n == 0) {
            JOptionPane.showMessageDialog(this, "No valid edges found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Layout nodes
        int cx = 400, cy = 250, r = 150;
        for (java.util.Map.Entry<String, Integer> entry : nodeMap.entrySet()) {
            int i = entry.getValue();
            int x = cx + (int)(r * Math.cos(2 * Math.PI * i / Math.max(1, n)));
            int y = cy + (int)(r * Math.sin(2 * Math.PI * i / Math.max(1, n)));
            gd.addNode(new GraphNode(i, x, y, entry.getKey()));
        }
        
        currentAlgorithm.setGraphData(gd);
        currentData = new int[n]; 
    }

    // ==========================================================
    //  Start / Pause / Reset
    // ==========================================================
    private void startAlgorithm() {
        if (currentAlgorithm == null || currentData == null) return;

        // Reset the algorithm counters
        currentAlgorithm.reset();
        currentAlgorithm.setDelay(controlPanel.getSpeedDelay());

        // Use a fresh copy so original is preserved
        final int[] workingData = originalData.clone();
        currentData = workingData;

        // Set target/tape if applicable
        if (currentAlgorithm.getCategory() == AlgorithmCategory.TOC) {
            String inputStr = controlPanel.getSearchTargetString();
            if (currentAlgorithm instanceof DFA dfa) {
                if (inputStr.isEmpty()) inputStr = "abaab"; // Default
                dfa.setInputString(inputStr);
            } else if (currentAlgorithm instanceof TuringMachine tm) {
                if (inputStr.isEmpty()) inputStr = "aabb"; // Default a^n b^n
                tm.setInputString(inputStr);
            }
        } else if (currentAlgorithm.getCategory() == AlgorithmCategory.SEARCHING) {
            int target = controlPanel.getSearchTarget();
            if (target < 0) {
                // Auto-pick a random value from the array
                target = workingData[random.nextInt(workingData.length)];
            }
            if (currentAlgorithm instanceof LinearSearch ls) {
                ls.setTarget(target);
            } else if (currentAlgorithm instanceof BinarySearch bs) {
                bs.setTarget(target);
            }
        }

        controlPanel.setRunningState(true);
        infoPanel.setStatus("Running…", new Color(245, 166, 35));
        lastResult = null;

        algorithmThread = new Thread(() -> {
            currentAlgorithm.execute(workingData, new AlgorithmCallback() {
                @Override
                public void onStep(int[] state, int idxA, int idxB, String action) {
                    if (isBarMode(currentAlgorithm)) {
                        vizPanel.updateState(state, idxA, idxB, action);
                    } else if (isGraphTreeMode(currentAlgorithm)) {
                        gtVizPanel.updateState(state, idxA, idxB, action);
                    } else {
                        sqVizPanel.updateState(state, idxA, idxB, action);
                    }
                    infoPanel.updateStats(
                            currentAlgorithm.getComparisons(),
                            currentAlgorithm.getSwaps(),
                            currentAlgorithm.getSteps());
                }

                @Override
                public void onComplete(AlgorithmResult result) {
                    lastResult = result;
                    if (isBarMode(currentAlgorithm)) {
                        vizPanel.markCompleted();
                    } else if (isGraphTreeMode(currentAlgorithm)) {
                        gtVizPanel.markCompleted();
                    } else {
                        sqVizPanel.markCompleted();
                    }
                    infoPanel.updateStats(result.getComparisons(),
                            result.getSwaps(), result.getSteps());
                    infoPanel.setStatus("Complete ✓", new Color(0, 230, 118));
                    controlPanel.setIdleState();
                }
            });
        }, "AlgorithmThread");
        algorithmThread.setDaemon(true);
        algorithmThread.start();
        controlPanel.getStartButton().setEnabled(false);
        controlPanel.getGenerateButton().setEnabled(false);
        controlPanel.getCustomInputButton().setEnabled(false);
        controlPanel.getPauseButton().setEnabled(true);
        controlPanel.getStepButton().setEnabled(true);
        controlPanel.getResetButton().setEnabled(true);
    }

    private void togglePause() {
        if (currentAlgorithm != null) {
            if (currentAlgorithm.isPaused()) {
                currentAlgorithm.resume();
                controlPanel.getPauseButton().setText("⏸ Pause");
                controlPanel.getStepButton().setEnabled(true);
                infoPanel.setStatus("Running…", new Color(245, 166, 35));
            } else {
                currentAlgorithm.pause();
                controlPanel.getPauseButton().setText("▶ Resume");
                controlPanel.getStepButton().setEnabled(true);
                infoPanel.setStatus("Paused", new Color(233, 69, 96));
            }
        }
    }

    private void stepAlgorithm() {
        if (currentAlgorithm != null && currentAlgorithm.isPaused()) {
             currentAlgorithm.stepForward();
        } else if (currentAlgorithm != null && !currentAlgorithm.isPaused()) {
             // If running, pause it then step
             currentAlgorithm.pause();
             controlPanel.getPauseButton().setText("▶ Resume");
             currentAlgorithm.stepForward();
        }
    }

    private void resetAlgorithm() {
        if (currentAlgorithm != null) {
            currentAlgorithm.cancel();
        }
        if (algorithmThread != null) {
            algorithmThread.interrupt();
        }
        currentData = originalData.clone();
        if (isBarMode(currentAlgorithm)) {
            vizPanel.setData(currentData.clone());
        } else if (isGraphTreeMode(currentAlgorithm)) {
            gtVizPanel.setData(currentData.clone());
        } else {
            sqVizPanel.setData(currentData.clone());
        }
        infoPanel.resetStats();
        infoPanel.setStatus("Reset", new Color(160, 160, 180));
        controlPanel.setIdleState();
        lastResult = null;
    }

    // ==========================================================
    //  Experiment save / load
    // ==========================================================
    private void saveExperiment() {
        if (lastResult == null) {
            JOptionPane.showMessageDialog(this,
                    "Run an algorithm first before saving an experiment.",
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Experiment exp = new Experiment();
        exp.setAlgorithmName(currentAlgorithm.getName());
        exp.setAlgorithmCategory(currentAlgorithm.getCategory().getDisplayName());
        exp.setInputArray(originalData.clone());
        exp.setResultArray(currentData.clone());
        exp.setComparisons(lastResult.getComparisons());
        exp.setSwaps(lastResult.getSwaps());
        exp.setSteps(lastResult.getSteps());
        exp.setElapsedTimeMs(lastResult.getElapsedTimeMs());
        exp.setSearchTarget(lastResult.getSearchTarget());
        exp.setSearchResult(lastResult.getSearchResult());

        ExperimentDialog.showSaveDialog(this, exp);
    }

    private void loadExperiment() {
        Experiment exp = ExperimentDialog.showLoadDialog(this);
        if (exp == null) return;

        // Find and select the matching algorithm
        for (Algorithm algo : algorithms) {
            if (algo.getName().equals(exp.getAlgorithmName())) {
                selectAlgorithm(algo);
                break;
            }
        }

        // Restore the input array
        if (exp.getInputArray() != null) {
            originalData = exp.getInputArray().clone();
            currentData = exp.getResultArray() != null
                    ? exp.getResultArray().clone()
                    : originalData.clone();
            if (isBarMode(currentAlgorithm)) {
                vizPanel.setData(currentData.clone());
            } else if (isGraphTreeMode(currentAlgorithm)) {
                gtVizPanel.setData(currentData.clone());
            } else {
                sqVizPanel.setData(currentData.clone());
            }
        }

        // Restore stats
        infoPanel.updateStats(exp.getComparisons(), exp.getSwaps(), exp.getSteps());
        infoPanel.setStatus("Loaded", new Color(0, 210, 255));

        // Store as lastResult so it can be re-saved
        lastResult = new AlgorithmResult(
                exp.getAlgorithmName(), exp.getComparisons(),
                exp.getSwaps(), exp.getSteps(), exp.getElapsedTimeMs());
        lastResult.setSearchTarget(exp.getSearchTarget());
        lastResult.setSearchResult(exp.getSearchResult());
    }
}
