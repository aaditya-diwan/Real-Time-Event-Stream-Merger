package com.streammerger.heaps;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Array-backed binary min-heap.
 * insert/extractMin: O(log n), peekMin: O(1)
 */
public class BinaryHeap<T extends Comparable<T>> implements Heap<T> {

    private final ArrayList<T> heap;
    private int size;

    public BinaryHeap() {
        this(16);
    }

    public BinaryHeap(int initialCapacity) {
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
    @Override public String getImplementationName() { return "BinaryHeap"; }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parent)) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int smallest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) smallest = left;
            if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) smallest = right;
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
