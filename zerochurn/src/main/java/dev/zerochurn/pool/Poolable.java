package dev.zerochurn.pool;

/**
 * Objects that can be wiped and reused. {@link #reset()} must clear every field
 * that a later borrower could observe.
 */
public interface Poolable {
    void reset();
}
