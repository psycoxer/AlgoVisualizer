package com.dsaviz.ui;

import com.dsaviz.experiment.Experiment;
import com.dsaviz.experiment.ExperimentManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Utility dialogs for saving and loading experiments.
 */
public class ExperimentDialog {

    private static final FileNameExtensionFilter FILTER =
            new FileNameExtensionFilter("DSA Experiment (*.dsaexp.json)", "json");

    /**
     * Shows a save-experiment dialog. Returns true if saved successfully.
     */
    public static boolean showSaveDialog(Component parent, Experiment experiment) {
        // Ask for notes
        String notes = JOptionPane.showInputDialog(parent,
                "Add notes to this experiment (optional):",
                "Save Experiment", JOptionPane.PLAIN_MESSAGE);
        if (notes == null) return false;  // user cancelled
        experiment.setNotes(notes);
        experiment.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // File chooser
        JFileChooser chooser = new JFileChooser(ExperimentManager.getDefaultDirectory());
        chooser.setFileFilter(FILTER);

        String suggestedName = experiment.getAlgorithmName().replaceAll("\\s+", "_").toLowerCase()
                + "_" + System.currentTimeMillis() + ".dsaexp.json";
        chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), suggestedName));

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".dsaexp.json")) {
                file = new File(file.getAbsolutePath() + ".dsaexp.json");
            }
            try {
                ExperimentManager.saveExperiment(experiment, file);
                JOptionPane.showMessageDialog(parent,
                        "Experiment saved to:\n" + file.getAbsolutePath(),
                        "Saved", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to save: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    /**
     * Shows a load-experiment dialog. Returns the loaded Experiment, or null.
     */
    public static Experiment showLoadDialog(Component parent) {
        JFileChooser chooser = new JFileChooser(ExperimentManager.getDefaultDirectory());
        chooser.setFileFilter(FILTER);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                Experiment exp = ExperimentManager.loadExperiment(file);

                // Build preview
                StringBuilder sb = new StringBuilder();
                sb.append("Algorithm: ").append(exp.getAlgorithmName()).append("\n");
                sb.append("Category:  ").append(exp.getAlgorithmCategory()).append("\n");
                sb.append("Date:      ").append(exp.getTimestamp()).append("\n");
                sb.append("Array:     ").append(arrayPreview(exp.getInputArray())).append("\n");
                sb.append("Comparisons: ").append(exp.getComparisons()).append("\n");
                sb.append("Swaps:       ").append(exp.getSwaps()).append("\n");
                sb.append("Steps:       ").append(exp.getSteps()).append("\n");
                sb.append("Time (ms):   ").append(exp.getElapsedTimeMs()).append("\n");
                if (exp.getSearchTarget() >= 0) {
                    sb.append("Search target: ").append(exp.getSearchTarget()).append("\n");
                    sb.append("Found at: ").append(exp.getSearchResult() >= 0
                            ? "index " + exp.getSearchResult() : "not found").append("\n");
                }
                if (exp.getNotes() != null && !exp.getNotes().isEmpty()) {
                    sb.append("Notes: ").append(exp.getNotes()).append("\n");
                }

                JOptionPane.showMessageDialog(parent, sb.toString(),
                        "Loaded Experiment", JOptionPane.INFORMATION_MESSAGE);
                return exp;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to load: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private static String arrayPreview(int[] arr) {
        if (arr == null) return "—";
        if (arr.length <= 10) return Arrays.toString(arr);
        return Arrays.toString(Arrays.copyOf(arr, 10))
                .replace("]", ", ... ] (length=" + arr.length + ")");
    }
}
