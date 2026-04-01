package com.streammerger.heaps;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Array-backed d-ary min-heap. Optimal d is typically 4-8 for cache performance.
 * insert/extractMin: O(d * log_d(n)), peekMin: O(1)
 */
public class DAryHeap<T extends Comparable<T>> implements Heap<T> {

    private final int d;
    private final ArrayList<T> heap;
    private int size;

    public DAryHeap() {
        this(4);
    }

    public DAryHeap(int d) {
        this(d, 16);
    }

    public DAryHeap(int d, int initialCapacity) {
        if (d < 2) throw new IllegalArgumentException("d must be >= 2");
        this.d = d;
        this.heap = new ArrayList<>(initialCapacity);
        this.size = 0;
    }

    @Override
    public void insert(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot insert null");
        heap.add(item);
        size++;
        siftUp(size - 1);
    }

    @Override
    public T extractMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T min = heap.get(0);
        heap.set(0, heap.get(size - 1));
        heap.remove(size - 1);
        size--;
        if (!isEmpty()) siftDown(0);
        return min;
    }

    @Override
    public T peekMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return heap.get(0);
    }

    @Override public boolean isEmpty() { return size == 0; }
    @Override public int size() { return size; }
    @Override public void clear() { heap.clear(); size = 0; }
    @Override public String getImplementationName() { return "DAryHeap(d=" + d + ")"; }
    public int getBranchingFactor() { return d; }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / d;
            if (heap.get(index).compareTo(heap.get(parent)) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int smallest = index;
            int firstChild = d * index + 1;
            for (int i = 0; i < d; i++) {
                int child = firstChild + i;
                if (child >= size) break;
                if (heap.get(child).compareTo(heap.get(smallest)) < 0) smallest = child;
            }
            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
