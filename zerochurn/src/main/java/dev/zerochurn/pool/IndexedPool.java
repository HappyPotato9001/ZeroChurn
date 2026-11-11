package dev.zerochurn.pool;

/**
 * Fixed free-list of preallocated instances. Borrow returns null when empty.
 */
public final class IndexedPool<T> {
    private final Object[] items;
    private final int[] free;
    private int freeTop;
    private final Factory<T> factory;

    public IndexedPool(int capacity, Factory<T> factory) {
        this.items = new Object[capacity];
        this.free = new int[capacity];
        this.factory = factory;
        for (int i = 0; i < capacity; i++) {
            this.items[i] = factory.create(i);
            this.free[i] = capacity - 1 - i;
        }
        this.freeTop = capacity;
    }

    @SuppressWarnings("unchecked")
    public T borrow() {
        if (this.freeTop == 0) {
            return null;
        }
        this.freeTop--;
        return (T) this.items[this.free[this.freeTop]];
    }

    public void release(int index) {
        if (index < 0 || index >= this.items.length || this.freeTop >= this.free.length) {
            return;
        }
        this.free[this.freeTop] = index;
        this.freeTop++;
    }

    public int capacity() {
        return this.items.length;
    }

    @FunctionalInterface
    public interface Factory<T> {
        T create(int index);
    }
}
