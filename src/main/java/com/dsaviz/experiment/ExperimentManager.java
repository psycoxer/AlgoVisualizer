package com.dsaviz.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of Experiment objects to/from JSON files.
 * File extension: .dsaexp.json
 */
public class ExperimentManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves an experiment to a JSON file.
     */
    public static void saveExperiment(Experiment exp, File file) throws IOException {
        // Ensure parent directory exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(exp, writer);
        }
    }

    /**
     * Loads an experiment from a JSON file.
     */
    public static Experiment loadExperiment(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            return GSON.fromJson(reader, Experiment.class);
        }
    }

    /**
     * Lists all experiment files in a directory.
     */
    public static List<File> listExperimentFiles(File directory) {
        List<File> files = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] found = directory.listFiles((dir, name) -> name.endsWith(".dsaexp.json"));
            if (found != null) {
                for (File f : found) {
                    files.add(f);
                }
            }
        }
        return files;
    }

    /**
     * Default experiments directory (relative to the project root).
     */
    public static File getDefaultDirectory() {
        File dir = new File("experiments");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
