package dev.zerochurn.pool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class Int3CacheTest {
    @Test
    void storesAndRetrievesExactKeys() {
        Int3Cache<String> cache = new Int3Cache<>(16);
        cache.put(1, 2, 3, "a");
        assertSame("a", cache.get(1, 2, 3));
        assertNull(cache.get(1, 2, 4));
        assertEquals(1L, cache.hits());
        assertEquals(1L, cache.misses());
    }
}
