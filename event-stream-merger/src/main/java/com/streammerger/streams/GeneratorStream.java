package com.streammerger.streams;

import com.streammerger.merger.Event;
import com.streammerger.utils.StreamRateType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generates synthetic time-ordered events for benchmarking.
 * Supports UNIFORM, POISSON, and BURSTY inter-arrival patterns.
 */
public class GeneratorStream implements StreamSource {

    private final int streamId;
    private final int totalEvents;
    private final StreamRateType rateType;
    private final Random random;

    private int generated;
    private long currentTimestamp;

    public GeneratorStream(int streamId, int totalEvents) {
        this(streamId, totalEvents, StreamRateType.UNIFORM, System.nanoTime());
    }

    public GeneratorStream(int streamId, int totalEvents, StreamRateType rateType, long seed) {
        this.streamId = streamId;
        this.totalEvents = totalEvents;
        this.rateType = rateType;
        this.random = new Random(seed);
        this.generated = 0;
        this.currentTimestamp = 0;
    }

    @Override
    public Event nextEvent() {
        if (generated >= totalEvents) return null;

        switch (rateType) {
            case UNIFORM  -> currentTimestamp += 1000;
            case POISSON  -> currentTimestamp += Math.max(1, (long) (-Math.log(random.nextDouble()) * 1000));
            case BURSTY   -> currentTimestamp += (generated % 100 < 20) ? 100 : 2000;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("value", random.nextDouble());
        data.put("sequence", generated);

        generated++;
        return new Event(currentTimestamp, streamId, data);
    }

    @Override public boolean hasMore() { return generated < totalEvents; }
    @Override public int getStreamId() { return streamId; }
    @Override public void close() {}
}
