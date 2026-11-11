package dev.zerochurn.pool;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Per-thread state with a reference-equality fast path for the Minecraft render
 * and server threads.
 */
public final class ThreadStates<T> {
    private final ThreadLocal<T> local;
    private final CopyOnWriteArrayList<T> all = new CopyOnWriteArrayList<>();
    private volatile Thread renderThread;
    private volatile Thread serverThread;
    private T renderState;
    private T serverState;

    public ThreadStates(Factory<T> factory) {
        this.local = new ThreadLocal<>() {
            @Override
            protected T initialValue() {
                T created = factory.create();
                ThreadStates.this.all.add(created);
                return created;
            }
        };
    }

    public T get() {
        Thread current = Thread.currentThread();
        if (current == this.renderThread) {
            return this.renderState;
        }
        if (current == this.serverThread) {
            return this.serverState;
        }
        return this.resolve(current);
    }

    public int size() {
        return this.all.size();
    }

    public T at(int index) {
        return this.all.get(index);
    }

    private T resolve(Thread current) {
        T state = this.local.get();
        String name = current.getName();
        if ("Render thread".equals(name) && this.renderThread == null) {
            this.renderState = state;
            this.renderThread = current;
        } else if ("Server thread".equals(name) && this.serverThread == null) {
            this.serverState = state;
            this.serverThread = current;
        }
        return state;
    }

    @FunctionalInterface
    public interface Factory<T> {
        T create();
    }
}
