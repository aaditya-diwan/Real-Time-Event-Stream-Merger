package com.streammerger.merger;

import com.streammerger.heaps.Heap;
import com.streammerger.streams.StreamSource;
import com.streammerger.utils.PerformanceStats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Merges K sorted streams into a single globally-sorted event stream.
 *
 * Algorithm: maintain a min-heap with the current head of each stream.
 * Each extraction yields the next smallest event across all streams.
 * Time complexity: O(N log K) where N = total events, K = number of streams.
 */
public class StreamMerger implements Iterable<Event>, AutoCloseable {

    private final Heap<HeapEntry> heap;
    private final List<StreamSource> streams;
    private final PerformanceStats stats;
    private boolean initialized;

    public StreamMerger(Heap<HeapEntry> heap) {
        this.heap = heap;
        this.streams = new ArrayList<>();
        this.stats = new PerformanceStats();
        this.initialized = false;
    }

    public void addStream(StreamSource stream) {
        if (initialized) throw new IllegalStateException("Cannot add streams after merge has started");
        streams.add(stream);
    }

    public int getStreamCount() { return streams.size(); }
    public PerformanceStats getStats() { return stats; }
    public Heap<HeapEntry> getHeap() { return heap; }

    @Override
    public Iterator<Event> iterator() { return new MergeIterator(); }

    @Override
    public void close() {
        streams.forEach(StreamSource::close);
    }

    private class MergeIterator implements Iterator<Event> {

        private Event nextEvent;

        MergeIterator() {
            initialize();
            advance();
        }

        private void initialize() {
            if (initialized) throw new IllegalStateException("Merge can only be iterated once");
            initialized = true;
            stats.start();
            for (int i = 0; i < streams.size(); i++) {
                Event first = streams.get(i).nextEvent();
                if (first != null) heap.insert(new HeapEntry(first, i));
            }
        }

        @Override
        public boolean hasNext() { return nextEvent != null; }

        @Override
        public Event next() {
            if (!hasNext()) throw new NoSuchElementException("No more events");
            Event current = nextEvent;
            advance();
            return current;
        }

        private void advance() {
            if (heap.isEmpty()) {
                nextEvent = null;
                stats.finish();
                return;
            }
            long t0 = System.nanoTime();
            HeapEntry entry = heap.extractMin();
            nextEvent = entry.getEvent();

            Event fromStream = streams.get(entry.getStreamIndex()).nextEvent();
            if (fromStream != null) heap.insert(new HeapEntry(fromStream, entry.getStreamIndex()));

            stats.recordEvent(System.nanoTime() - t0);
        }
    }
}
