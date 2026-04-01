package com.streammerger.merger;

/**
 * Wraps an event with its source stream index so we know which stream to advance after extraction.
 */
public class HeapEntry implements Comparable<HeapEntry> {

    private final Event event;
    private final int streamIndex;

    public HeapEntry(Event event, int streamIndex) {
        this.event = event;
        this.streamIndex = streamIndex;
    }

    public Event getEvent() { return event; }
    public int getStreamIndex() { return streamIndex; }

    @Override
    public int compareTo(HeapEntry other) { return this.event.compareTo(other.event); }

    @Override
    public String toString() {
        return String.format("HeapEntry{event=%s, streamIndex=%d}", event, streamIndex);
    }
}
