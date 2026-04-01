package com.streammerger.heaps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DAryHeapTest {

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 8, 16})
    void minHeapOrderingForAllD(int d) {
        DAryHeap<Integer> heap = new DAryHeap<>(d);
        Random rng = new Random(d);
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            int v = rng.nextInt(10000);
            values.add(v);
            heap.insert(v);
        }
        Collections.sort(values);
        for (int expected : values) assertEquals(expected, heap.extractMin());
    }

    @Test void invalidD() {
        assertThrows(IllegalArgumentException.class, () -> new DAryHeap<>(1));
    }

    @Test void branchingFactor() {
        assertEquals(4, new DAryHeap<>(4).getBranchingFactor());
    }

    @Test void extractMinOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> new DAryHeap<Integer>().extractMin());
    }

    @Test void clear() {
        DAryHeap<Integer> heap = new DAryHeap<>();
        heap.insert(1); heap.insert(2);
        heap.clear();
        assertTrue(heap.isEmpty());
    }
}
