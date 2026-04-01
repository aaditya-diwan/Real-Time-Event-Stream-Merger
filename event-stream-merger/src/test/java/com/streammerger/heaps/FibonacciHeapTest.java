package com.streammerger.heaps;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FibonacciHeapTest {

    @Test void basicOrdering() {
        FibonacciHeap<Integer> heap = new FibonacciHeap<>();
        for (int v : new int[]{5, 1, 9, 3, 7}) heap.insert(v);
        assertEquals(1, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(5, heap.extractMin());
    }

    @Test void largeRandomInsertions() {
        FibonacciHeap<Integer> heap = new FibonacciHeap<>();
        Random rng = new Random(99);
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            int v = rng.nextInt(10000);
            values.add(v);
            heap.insert(v);
        }
        Collections.sort(values);
        for (int expected : values) assertEquals(expected, heap.extractMin());
    }

    @Test void emptyExceptionOnExtract() {
        assertThrows(NoSuchElementException.class, () -> new FibonacciHeap<Integer>().extractMin());
    }

    @Test void emptyExceptionOnPeek() {
        assertThrows(NoSuchElementException.class, () -> new FibonacciHeap<Integer>().peekMin());
    }

    @Test void insertNull() {
        assertThrows(IllegalArgumentException.class, () -> new FibonacciHeap<Integer>().insert(null));
    }

    @Test void clear() {
        FibonacciHeap<Integer> heap = new FibonacciHeap<>();
        heap.insert(1); heap.clear();
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }
}
