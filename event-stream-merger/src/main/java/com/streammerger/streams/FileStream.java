package com.streammerger.streams;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.streammerger.merger.Event;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads events from a newline-delimited JSON file.
 * Each line must be a JSON object with at least a "timestamp" (long) field.
 */
public class FileStream implements StreamSource {

    private final int streamId;
    private final BufferedReader reader;
    private final Gson gson;
    private Event buffered;
    private boolean closed;

    public FileStream(int streamId, String filepath) throws IOException {
        this.streamId = streamId;
        this.reader = new BufferedReader(new FileReader(filepath));
        this.gson = new Gson();
        this.closed = false;
        this.buffered = readNext();
    }

    @Override
    public Event nextEvent() {
        Event current = buffered;
        if (current != null) buffered = readNext();
        return current;
    }

    @Override public boolean hasMore() { return buffered != null; }
    @Override public int getStreamId() { return streamId; }

    @Override
    public void close() {
        if (!closed) {
            try { reader.close(); } catch (IOException ignored) {}
            closed = true;
        }
    }

    private Event readNext() {
        if (closed) return null;
        try {
            String line = reader.readLine();
            if (line == null) { close(); return null; }
            JsonObject json = gson.fromJson(line, JsonObject.class);
            long timestamp = json.get("timestamp").getAsLong();
            Map<String, Object> data = new HashMap<>();
            json.entrySet().stream()
                .filter(e -> !"timestamp".equals(e.getKey()))
                .forEach(e -> data.put(e.getKey(), e.getValue()));
            return new Event(timestamp, streamId, data);
        } catch (IOException e) {
            close();
            return null;
        }
    }
}
