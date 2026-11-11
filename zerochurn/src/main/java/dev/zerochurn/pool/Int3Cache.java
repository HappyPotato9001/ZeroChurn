package dev.zerochurn.pool;

/**
 * Direct-mapped cache keyed by three ints. Values are never mutated after put,
 * so cached immutables are safe to escape into long-lived structures.
 */
public final class Int3Cache<T> {
    private final Object[] values;
    private final int[] xs;
    private final int[] ys;
    private final int[] zs;
    private final int mask;
    private long hits;
    private long misses;

    public Int3Cache(int requestedCapacity) {
        int capacity = ThreadLocalPool.nextPowerOfTwo(requestedCapacity);
        this.values = new Object[capacity];
        this.xs = new int[capacity];
        this.ys = new int[capacity];
        this.zs = new int[capacity];
        this.mask = capacity - 1;
    }

    public int capacity() {
        return this.values.length;
    }

    public long hits() {
        return this.hits;
    }

    public long misses() {
        return this.misses;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y, int z) {
        int index = mix(x, y, z) & this.mask;
        if (this.values[index] != null && this.xs[index] == x && this.ys[index] == y && this.zs[index] == z) {
            this.hits++;
            return (T) this.values[index];
        }
        this.misses++;
        return null;
    }

    public void put(int x, int y, int z, T value) {
        if (value == null) {
            return;
        }
        int index = mix(x, y, z) & this.mask;
        this.values[index] = value;
        this.xs[index] = x;
        this.ys[index] = y;
        this.zs[index] = z;
    }

    private static int mix(int x, int y, int z) {
        int h = x * 73428767 ^ y * 912346789 ^ z * 137341;
        h ^= (h >>> 16);
        return h;
    }
}
