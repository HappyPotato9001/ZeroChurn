package dev.zerochurn.pool;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Fixed-capacity ring buffer of reusable instances, isolated per thread.
 * Capacity is rounded up to the next power of two so index updates stay a
 * single bitwise mask. Empty slots are filled on first acquire; when the ring
 * is full a fresh object is allocated and counted as a miss.
 */
public final class ThreadLocalPool<T> {
    private final ThreadLocal<Ring<T>> local;
    private final CopyOnWriteArrayList<Ring<T>> rings = new CopyOnWriteArrayList<>();
    private final Supplier<T> factory;
    private final Resetter<T> resetter;
    private final int capacity;
    private final int mask;

    public ThreadLocalPool(int requestedCapacity, Supplier<T> factory, Resetter<T> resetter) {
        if (factory == null || resetter == null) {
            throw new NullPointerException("factory and resetter are required");
        }
        this.capacity = nextPowerOfTwo(requestedCapacity);
        this.mask = this.capacity - 1;
        this.factory = factory;
        this.resetter = resetter;
        this.local = ThreadLocal.withInitial(this::newRing);
    }

    public static <T extends Poolable> ThreadLocalPool<T> of(int capacity, Supplier<T> factory) {
        return new ThreadLocalPool<>(capacity, factory, Poolable::reset);
    }

    public T acquire() {
        Ring<T> ring = this.local.get();
        T value = ring.take();
        if (value == null) {
            value = this.factory.get();
            ring.misses++;
        } else {
            ring.hits++;
        }
        this.resetter.reset(value);
        return value;
    }

    public void release(T value) {
        if (value == null) {
            return;
        }
        this.local.get().give(value);
    }

    /**
     * Drops this thread's ring so pooled instances cannot leak across a worker
     * that is being retired (C2ME / custom executors). Safe to call when no
     * ring was ever created for the current thread.
     */
    public void discardThreadState() {
        Ring<T> ring = this.local.get();
        if (ring != null) {
            ring.clear();
            this.rings.remove(ring);
        }
        this.local.remove();
    }

    public int capacity() {
        return this.capacity;
    }

    public long hits() {
        return this.snapshot().hits;
    }

    public long misses() {
        return this.snapshot().misses;
    }

    public Snapshot snapshot() {
        long hits = 0L;
        long misses = 0L;
        for (int i = 0; i < this.rings.size(); i++) {
            Ring<T> ring = this.rings.get(i);
            hits += ring.hits;
            misses += ring.misses;
        }
        return new Snapshot(hits, misses);
    }

    public static int nextPowerOfTwo(int value) {
        int size = 1;
        int requested = Math.max(1, value);
        while (size < requested) {
            size <<= 1;
            if (size <= 0) {
                return 1 << 30;
            }
        }
        return size;
    }

    private Ring<T> newRing() {
        Ring<T> ring = new Ring<>(this.capacity, this.mask);
        this.rings.add(ring);
        return ring;
    }

    @FunctionalInterface
    public interface Resetter<T> {
        void reset(T value);
    }

    public static final class Snapshot {
        public final long hits;
        public final long misses;

        public Snapshot(long hits, long misses) {
            this.hits = hits;
            this.misses = misses;
        }
    }

    private static final class Ring<T> {
        private final Object[] slots;
        private final int mask;
        private int head;
        private int size;
        private long hits;
        private long misses;

        private Ring(int capacity, int mask) {
            this.slots = new Object[capacity];
            this.mask = mask;
        }

        @SuppressWarnings("unchecked")
        private T take() {
            if (this.size == 0) {
                return null;
            }
            int index = (this.head - this.size) & this.mask;
            T value = (T) this.slots[index];
            this.slots[index] = null;
            this.size--;
            return value;
        }

        private void give(T value) {
            if (this.size >= this.slots.length) {
                return;
            }
            this.slots[this.head] = value;
            this.head = (this.head + 1) & this.mask;
            this.size++;
        }

        private void clear() {
            for (int i = 0; i < this.slots.length; i++) {
                this.slots[i] = null;
            }
            this.head = 0;
            this.size = 0;
        }
    }
}
