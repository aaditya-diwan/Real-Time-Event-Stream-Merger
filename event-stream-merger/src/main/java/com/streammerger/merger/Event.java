package com.streammerger.merger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event implements Comparable<Event> {

    private final long timestamp;
    private final int streamId;
    private final Map<String, Object> data;

    public Event(long timestamp, int streamId) {
        this(timestamp, streamId, new HashMap<>());
    }

    public Event(long timestamp, int streamId, Map<String, Object> data) {
        this.timestamp = timestamp;
        this.streamId = streamId;
        this.data = new HashMap<>(data);
    }

    public long getTimestamp() { return timestamp; }
    public int getStreamId() { return streamId; }
    public Map<String, Object> getData() { return new HashMap<>(data); }
    public Object getDataValue(String key) { return data.get(key); }

    @Override
    public int compareTo(Event other) {
        int cmp = Long.compare(this.timestamp, other.timestamp);
        return cmp != 0 ? cmp : Integer.compare(this.streamId, other.streamId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event other)) return false;
        return timestamp == other.timestamp && streamId == other.streamId;
    }

    @Override
    public int hashCode() { return Objects.hash(timestamp, streamId); }

    @Override
    public String toString() {
        return String.format("Event{ts=%d, stream=%d, data=%s}", timestamp, streamId, data);
    }
}
