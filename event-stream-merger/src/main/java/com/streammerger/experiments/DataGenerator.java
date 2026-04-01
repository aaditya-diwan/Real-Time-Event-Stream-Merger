package com.streammerger.experiments;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generates test data files in newline-delimited JSON format.
 */
public class DataGenerator {

    private final Gson gson = new Gson();

    public void generateStream(String filepath, int numEvents, long startTimestamp,
                               long intervalMs, long seed) throws IOException {
        new File(filepath).getParentFile().mkdirs();
        Random random = new Random(seed);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            long ts = startTimestamp;
            for (int i = 0; i < numEvents; i++) {
                ts += intervalMs + (long) (random.nextGaussian() * intervalMs * 0.1);
                ts = Math.max(ts, startTimestamp);

                Map<String, Object> event = new HashMap<>();
                event.put("timestamp", ts);
                event.put("value", random.nextDouble() * 100);
                event.put("sequence", i);
                writer.println(gson.toJson(event));
            }
        }
    }

    public void generateTestDataset(String dir, int numStreams, int eventsPerStream) throws IOException {
        long baseTimestamp = System.currentTimeMillis();
        for (int i = 0; i < numStreams; i++) {
            generateStream(dir + "/stream_" + i + ".jsonl",
                eventsPerStream, baseTimestamp, 100 + i * 50L, i);
        }
        System.out.printf("Generated %d streams with %d events each in %s%n",
            numStreams, eventsPerStream, dir);
    }
}
