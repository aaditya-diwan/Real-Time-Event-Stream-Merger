package com.streammerger.experiments;

import java.io.*;
import java.util.*;

/**
 * Parses CSV result files and prints summary statistics.
 */
public class ResultsAnalyzer {

    public void analyzeFile(String csvPath) throws IOException {
        System.out.println("Analyzing: " + csvPath);
        System.out.println("-".repeat(60));

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String header = reader.readLine();
            if (header == null) return;
            String[] columns = header.split(",");
            System.out.println("Columns: " + Arrays.toString(columns));

            Map<String, List<Double>> throughputByGroup = new LinkedHashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                String group = parts[0];
                // Find ThroughputEventsPerSec column
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].contains("Throughput") && i < parts.length) {
                        throughputByGroup.computeIfAbsent(group, k -> new ArrayList<>())
                            .add(Double.parseDouble(parts[i]));
                        break;
                    }
                }
            }

            throughputByGroup.forEach((group, values) -> {
                double avg = values.stream().mapToDouble(d -> d).average().orElse(0);
                double max = values.stream().mapToDouble(d -> d).max().orElse(0);
                System.out.printf("  %-25s avg=%.0f evt/s  max=%.0f evt/s%n", group, avg, max);
            });
        }
        System.out.println();
    }

    public void analyzeAllResults(String resultsDir) throws IOException {
        File dir = new File(resultsDir);
        if (!dir.exists()) { System.out.println("No results directory found."); return; }
        File[] csvFiles = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) { System.out.println("No CSV files found."); return; }
        Arrays.sort(csvFiles);
        for (File f : csvFiles) analyzeFile(f.getPath());
    }
}
