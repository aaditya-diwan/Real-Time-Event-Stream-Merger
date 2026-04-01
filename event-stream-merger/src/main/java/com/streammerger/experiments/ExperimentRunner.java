package com.streammerger.experiments;

import com.streammerger.heaps.*;
import com.streammerger.merger.Event;
import com.streammerger.merger.HeapEntry;
import com.streammerger.merger.StreamMerger;
import com.streammerger.streams.GeneratorStream;
import com.streammerger.streams.StreamSource;
import com.streammerger.utils.PerformanceStats;
import com.streammerger.utils.StreamRateType;

import java.io.*;
import java.util.List;

public class ExperimentRunner {

    private static final String RESULTS_DIR = "results";

    public static void main(String[] args) throws IOException {
        new File(RESULTS_DIR).mkdirs();
        ExperimentRunner runner = new ExperimentRunner();

        System.out.println("=== Real-Time Event Stream Merger: Experiments ===\n");

        runner.experiment1_HeapComparison();
        runner.experiment2_DAryTuning();
        runner.experiment3_Scalability();
        runner.experiment4_MixedRates();
        runner.experiment5_LargeScale();

        System.out.println("\nAll experiments done. Results in ./" + RESULTS_DIR + "/");

        new ResultsAnalyzer().analyzeAllResults(RESULTS_DIR);
    }

    /** Compare Binary, 4-ary, and Fibonacci heaps on the same workload. */
    private void experiment1_HeapComparison() throws IOException {
        System.out.println("Experiment 1: Heap Type Comparison");
        int K = 10, eventsPerStream = 100_000, runs = 5;

        List<Heap<HeapEntry>> heaps = List.of(
            new BinaryHeap<>(), new DAryHeap<>(4), new FibonacciHeap<>()
        );

        try (PrintWriter w = csv("experiment1.csv")) {
            w.println("HeapType,Run,TimeMs,ThroughputEventsPerSec,MemMB,P50us,P95us,P99us");
            for (Heap<HeapEntry> heap : heaps) {
                for (int run = 0; run < runs; run++) {
                    PerformanceStats s = runMerge(heap, K, eventsPerStream, StreamRateType.UNIFORM);
                    w.printf("%s,%d,%d,%.0f,%d,%.1f,%.1f,%.1f%n",
                        heap.getImplementationName(), run, s.getElapsedTimeMs(),
                        s.getThroughput(), s.getMemoryUsageMB(),
                        s.getLatencyPercentile(50), s.getLatencyPercentile(95), s.getLatencyPercentile(99));
                }
                System.out.printf("  %-20s done%n", heap.getImplementationName());
            }
        }
    }

    /** Find optimal d for d-ary heap. */
    private void experiment2_DAryTuning() throws IOException {
        System.out.println("Experiment 2: d-ary Parameter Tuning");
        int K = 10, eventsPerStream = 100_000, runs = 5;
        int[] dValues = {2, 4, 8, 16, 32};

        try (PrintWriter w = csv("experiment2.csv")) {
            w.println("d,Run,TimeMs,ThroughputEventsPerSec,MemMB");
            for (int d : dValues) {
                for (int run = 0; run < runs; run++) {
                    PerformanceStats s = runMerge(new DAryHeap<>(d), K, eventsPerStream, StreamRateType.UNIFORM);
                    w.printf("%d,%d,%d,%.0f,%d%n", d, run,
                        s.getElapsedTimeMs(), s.getThroughput(), s.getMemoryUsageMB());
                }
                System.out.printf("  d=%-3d done%n", d);
            }
        }
    }

    /** Measure how throughput scales with number of streams K. */
    private void experiment3_Scalability() throws IOException {
        System.out.println("Experiment 3: Scalability (varying K)");
        int eventsPerStream = 10_000, runs = 3;
        int[] kValues = {2, 5, 10, 20, 50, 100, 200};

        try (PrintWriter w = csv("experiment3.csv")) {
            w.println("K,TotalEvents,Run,TimeMs,ThroughputEventsPerSec");
            for (int K : kValues) {
                for (int run = 0; run < runs; run++) {
                    PerformanceStats s = runMerge(new DAryHeap<>(4), K, eventsPerStream, StreamRateType.UNIFORM);
                    w.printf("%d,%d,%d,%d,%.0f%n", K, (long) K * eventsPerStream, run,
                        s.getElapsedTimeMs(), s.getThroughput());
                }
                System.out.printf("  K=%-4d done%n", K);
            }
        }
    }

    /** Compare latency under uniform vs. mixed stream rates. */
    private void experiment4_MixedRates() throws IOException {
        System.out.println("Experiment 4: Stream Rate Variability");
        int K = 10, eventsPerStream = 10_000, runs = 5;

        try (PrintWriter w = csv("experiment4.csv")) {
            w.println("RateType,Run,P50us,P95us,P99us");
            for (StreamRateType rate : StreamRateType.values()) {
                for (int run = 0; run < runs; run++) {
                    PerformanceStats s = runMerge(new DAryHeap<>(4), K, eventsPerStream, rate);
                    w.printf("%s,%d,%.1f,%.1f,%.1f%n", rate, run,
                        s.getLatencyPercentile(50), s.getLatencyPercentile(95), s.getLatencyPercentile(99));
                }
                System.out.printf("  %-10s done%n", rate);
            }
        }
    }

    /** Single large-scale run: 100 streams × 10k events = 1M total. */
    private void experiment5_LargeScale() throws IOException {
        System.out.println("Experiment 5: Large-Scale (100 streams × 10k = 1M events)");
        PerformanceStats s = runMerge(new DAryHeap<>(4), 100, 10_000, StreamRateType.UNIFORM);
        System.out.print("  ");
        s.printSummary();

        try (PrintWriter w = csv("experiment5.csv")) {
            w.println("K,Events,TimeMs,ThroughputEventsPerSec,MemMB");
            w.printf("100,1000000,%d,%.0f,%d%n",
                s.getElapsedTimeMs(), s.getThroughput(), s.getMemoryUsageMB());
        }
    }

    private PerformanceStats runMerge(Heap<HeapEntry> heap, int K, int eventsPerStream,
                                      StreamRateType rateType) {
        StreamMerger merger = new StreamMerger(heap);
        for (int i = 0; i < K; i++) {
            merger.addStream(new GeneratorStream(i, eventsPerStream, rateType, i));
        }
        for (Event ignored : merger) {}
        merger.close();
        return merger.getStats();
    }

    private PrintWriter csv(String filename) throws IOException {
        return new PrintWriter(new FileWriter(RESULTS_DIR + "/" + filename));
    }
}
