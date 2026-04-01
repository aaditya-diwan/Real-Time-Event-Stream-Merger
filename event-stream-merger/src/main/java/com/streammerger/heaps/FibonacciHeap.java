package com.streammerger.heaps;

import java.util.NoSuchElementException;

/**
 * Fibonacci heap with lazy consolidation.
 * Amortized: insert O(1), extractMin O(log n), merge O(1).
 * High constant factors make this slower in practice for small workloads.
 */
public class FibonacciHeap<T extends Comparable<T>> implements Heap<T> {

    private static class Node<T> {
        T item;
        Node<T> parent, child, left, right;
        int degree;
        boolean marked;

        Node(T item) {
            this.item = item;
            this.left = this;
            this.right = this;
        }
    }

    private Node<T> minNode;
    private int size;

    @Override
    public void insert(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot insert null");
        Node<T> node = new Node<>(item);
        if (minNode == null) {
            minNode = node;
        } else {
            addToRootList(node);
            if (item.compareTo(minNode.item) < 0) minNode = node;
        }
        size++;
    }

    @Override
    public T extractMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        Node<T> z = minNode;

        if (z.child != null) {
            Node<T> child = z.child;
            do {
                Node<T> next = child.right;
                addToRootList(child);
                child.parent = null;
                child = next;
            } while (child != z.child);
        }

        removeFromRootList(z);

        if (z == z.right) {
            minNode = null;
        } else {
            minNode = z.right;
            consolidate();
        }

        size--;
        return z.item;
    }

    @Override
    public T peekMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return minNode.item;
    }

    @Override public boolean isEmpty() { return minNode == null; }
    @Override public int size() { return size; }
    @Override public void clear() { minNode = null; size = 0; }
    @Override public String getImplementationName() { return "FibonacciHeap"; }

    private void addToRootList(Node<T> node) {
        node.right = minNode.right;
        node.left = minNode;
        minNode.right.left = node;
        minNode.right = node;
    }

    private void removeFromRootList(Node<T> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidate() {
        int maxDegree = (int) Math.ceil(Math.log(size + 1) / Math.log(2)) + 2;
        @SuppressWarnings("unchecked")
        Node<T>[] degreeTable = new Node[maxDegree];

        // Collect all root nodes into a list first to avoid ConcurrentModification
        java.util.List<Node<T>> roots = new java.util.ArrayList<>();
        Node<T> current = minNode;
        do {
            roots.add(current);
            current = current.right;
        } while (current != minNode);

        for (Node<T> root : roots) {
            int degree = root.degree;
            while (degreeTable[degree] != null) {
                Node<T> other = degreeTable[degree];
                if (root.item.compareTo(other.item) > 0) {
                    Node<T> tmp = root; root = other; other = tmp;
                }
                link(other, root);
                degreeTable[degree] = null;
                degree++;
            }
            degreeTable[degree] = root;
        }

        minNode = null;
        for (Node<T> node : degreeTable) {
            if (node == null) continue;
            node.left = node;
            node.right = node;
            if (minNode == null) {
                minNode = node;
            } else {
                addToRootList(node);
                if (node.item.compareTo(minNode.item) < 0) minNode = node;
            }
        }
    }

    private void link(Node<T> child, Node<T> parent) {
        // child is already removed from root list via consolidate loop
        if (parent.child == null) {
            parent.child = child;
            child.left = child;
            child.right = child;
        } else {
            child.right = parent.child.right;
            child.left = parent.child;
            parent.child.right.left = child;
            parent.child.right = child;
        }
        child.parent = parent;
        parent.degree++;
        child.marked = false;
    }
}
