package dev.zerochurn.pool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class ThreadLocalPoolTest {
    @Test
    void roundsCapacityToPowerOfTwo() {
        assertEquals(64, ThreadLocalPool.nextPowerOfTwo(33));
        assertEquals(32, ThreadLocalPool.of(32, Scratch::new).capacity());
    }

    @Test
    void reusesReleasedInstances() {
        ThreadLocalPool<Scratch> pool = ThreadLocalPool.of(4, Scratch::new);
        Scratch first = pool.acquire();
        first.value = 7;
        pool.release(first);

        Scratch second = pool.acquire();
        assertSame(first, second);
        assertEquals(0, second.value);
        assertEquals(1L, pool.hits());
        assertEquals(1L, pool.misses());
    }

    @Test
    void allocatesWhenRingIsEmpty() {
        ThreadLocalPool<Scratch> pool = ThreadLocalPool.of(2, Scratch::new);
        Scratch a = pool.acquire();
        Scratch b = pool.acquire();
        assertNotSame(a, b);
        assertEquals(0L, pool.hits());
        assertEquals(2L, pool.misses());
    }

    @Test
    void discardThreadStateClearsCurrentRing() {
        ThreadLocalPool<Scratch> pool = ThreadLocalPool.of(4, Scratch::new);
        Scratch value = pool.acquire();
        pool.release(value);
        assertEquals(1L, pool.hits() + pool.misses());
        pool.discardThreadState();
        Scratch next = pool.acquire();
        assertNotSame(value, next);
        assertEquals(1L, pool.misses());
        assertEquals(0L, pool.hits());
    }

    @Test
    void dropsOverflowInsteadOfGrowing() {
        ThreadLocalPool<Scratch> pool = ThreadLocalPool.of(2, Scratch::new);
        Scratch a = pool.acquire();
        Scratch b = pool.acquire();
        Scratch c = pool.acquire();
        pool.release(a);
        pool.release(b);
        pool.release(c);

        Scratch first = pool.acquire();
        Scratch second = pool.acquire();
        Scratch third = pool.acquire();
        assertSame(a, first);
        assertSame(b, second);
        assertNotSame(c, third);
        assertEquals(2L, pool.hits());
        assertEquals(4L, pool.misses());
    }

    private static final class Scratch implements Poolable {
        private int value;

        @Override
        public void reset() {
            this.value = 0;
        }
    }
}
