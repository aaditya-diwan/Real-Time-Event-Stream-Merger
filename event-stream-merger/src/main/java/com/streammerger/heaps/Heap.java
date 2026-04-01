package com.streammerger.heaps;

import java.util.NoSuchElementException;

public interface Heap<T extends Comparable<T>> {
    void insert(T item);
    T extractMin();
    T peekMin();
    boolean isEmpty();
    int size();
    void clear();
    String getImplementationName();
}
