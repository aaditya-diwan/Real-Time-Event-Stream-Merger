package com.streammerger.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerformanceStats {

    private long startTime;
    private long endTime;
    private long eventCount;
    private final List<Long> latenciesNs;

    public PerformanceStats() {
        this.latenciesNs = new ArrayList<>();
    }

    public void start() { this.startTime = System.nanoTime(); }
    public void finish() { this.endTime = System.nanoTime(); }

    public void recordEvent(long latencyNanos) {
        eventCount++;
        latenciesNs.add(latencyNanos);
    }

    public long getEventCount() { return eventCount; }

    public long getElapsedTimeMs() {
        long end = endTime == 0 ? System.nanoTime() : endTime;
        return (end - startTime) / 1_000_000;
    }

    public double getThroughput() {
        long ms = getElapsedTimeMs();
        return ms == 0 ? 0 : (eventCount * 1000.0) / ms;
    }

    /** Returns latency at the given percentile in microseconds. */
    public double getLatencyPercentile(double percentile) {
        if (latenciesNs.isEmpty()) return 0;
        List<Long> sorted = new ArrayList<>(latenciesNs);
        Collections.sort(sorted);
        int index = Math.max(0, (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1);
        return sorted.get(index) / 1000.0;
    }

    public long getMemoryUsageMB() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        return (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
    }

    public String getSummary() {
        return String.format(
            "Events: %d | Time: %dms | Throughput: %.0f evt/s | Latency p50=%.1fμs p95=%.1fμs p99=%.1fμs | Mem: %dMB",
            eventCount, getElapsedTimeMs(), getThroughput(),
            getLatencyPercentile(50), getLatencyPercentile(95), getLatencyPercentile(99),
            getMemoryUsageMB()
        );
    }

    public void printSummary() { System.out.println(getSummary()); }
}
