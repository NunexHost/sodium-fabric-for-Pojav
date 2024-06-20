package me.jellysquid.mods.sodium.client.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class DoubleBufferedQueue<E> {
    private static final int INITIAL_CAPACITY = 256;
    private static final int MAX_CAPACITY = 1 << 30; // Max array size as a power of two

    private E[] readElements, writeElements;
    private int readIndex, writeIndex;

    public DoubleBufferedQueue() {
        this.readElements = createArray(INITIAL_CAPACITY);
        this.writeElements = createArray(INITIAL_CAPACITY);
    }

    public boolean flip() {
        if (writeIndex == 0) {
            return false;
        }

        E[] tmp = readElements;
        readElements = writeElements;
        writeElements = tmp;

        writeIndex = 0;
        return true;
    }

    public void reset() {
        readIndex = 0;
        writeIndex = 0;
    }

    public ReadQueue<E> read() {
        return new QueueView<>(readElements, readIndex, writeIndex);
    }

    public WriteQueue<E> write() {
        return new QueueView<>(writeElements, writeIndex, writeElements.length);
    }

    private static class QueueView<E> implements ReadQueue<E>, WriteQueue<E> {
        private final E[] elements;
        private final int startIndex, endIndex;

        QueueView(E[] elements, int startIndex, int endIndex) {
            this.elements = elements;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void ensureCapacity(int numElements) {
            // Check if more capacity is needed and resize if necessary
            if (startIndex + numElements > elements.length) {
                grow(startIndex + numElements);
            }
        }

        @Override
        public E dequeue() {
            if (startIndex >= endIndex) {
                return null;
            }
            return elements[startIndex++];
        }

        @Override
        public void enqueue(E e) {
            elements[endIndex++] = e;
        }

        @Override
        public void clear() {
            Arrays.fill(elements, startIndex, endIndex, null);
            startIndex = 0;
            endIndex = 0;
        }

        @Override
        public int size() {
            return endIndex - startIndex;
        }

        private void grow(int minCapacity) {
            int newCapacity = Math.min(MAX_CAPACITY, getNextPowerOfTwo(minCapacity));
            elements = Arrays.copyOf(elements, newCapacity);
        }

        private static int getNextPowerOfTwo(int n) {
            int highestOneBit = Integer.highestOneBit(n);
            return (highestOneBit == n) ? n : highestOneBit << 1;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> E[] createArray(int capacity) {
        return (E[]) new Object[capacity];
    }
}
