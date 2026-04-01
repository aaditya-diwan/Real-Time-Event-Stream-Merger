package com.streammerger.streams;

import com.streammerger.merger.Event;

public interface StreamSource extends AutoCloseable {
    Event nextEvent();
    boolean hasMore();
    int getStreamId();
    @Override void close();
}
