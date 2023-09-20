package shape.utils;

import java.util.ArrayDeque;

public class AveragingCircularBuffer<T extends Number> {
    private ArrayDeque<T> buffer;
    private final int maxSize;
    private double sum;

    public AveragingCircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        buffer = new ArrayDeque<>(capacity);
        maxSize = capacity;
        sum = 0.0;
    }

    public void add(T value) {
        if (buffer.size() >= maxSize) {
            T removedValue = buffer.removeFirst();
            sum -= removedValue.doubleValue();
        }
        buffer.addLast(value);
        sum += value.doubleValue();
    }

    public double getAverage() {
        if (buffer.isEmpty()) {
            return 0.0;
        }
        return sum / buffer.size();
    }

    public int size() {
        return buffer.size();
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }
}