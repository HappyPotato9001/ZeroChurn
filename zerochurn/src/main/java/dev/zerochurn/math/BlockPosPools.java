package dev.zerochurn.math;

import dev.zerochurn.config.ZeroChurnConfig;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.pool.IndexedPool;
import dev.zerochurn.pool.Int3Cache;
import dev.zerochurn.pool.ThreadStates;
import net.minecraft.core.BlockPos;

import java.util.Iterator;

/**
 * Interns immutable {@link BlockPos} results from {@code containing}, and pools
 * iterators for {@code betweenClosed}. Cached positions may escape; pooled
 * iterators must stay inside the for-each that borrowed them.
 */
public final class BlockPosPools {
    public static final long BYTES_PER_POSITION = 32L;
    public static final long BYTES_PER_ITERATOR = 96L;

    private static volatile ThreadStates<Local> states;

    private BlockPosPools() {
    }

    public static boolean enabled() {
        return ZeroChurnConfigLoader.get().poolBlockPos;
    }

    public static BlockPos get(int x, int y, int z) {
        if (!enabled()) {
            return null;
        }
        return local().positions.get(x, y, z);
    }

    public static void store(int x, int y, int z, BlockPos value) {
        if (!enabled() || value == null) {
            return;
        }
        local().positions.put(x, y, z, value);
    }

    public static Iterable<BlockPos> tryBetweenClosed(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        if (!enabled()) {
            return null;
        }
        if (endX < startX || endY < startY || endZ < startZ) {
            return null;
        }
        int sizeX = endX - startX + 1;
        int sizeY = endY - startY + 1;
        int sizeZ = endZ - startZ + 1;
        long product = (long) sizeX * (long) sizeY * (long) sizeZ;
        if (product <= 0L || product > Integer.MAX_VALUE) {
            return null;
        }
        return new BlockPosIteration(startX, startY, startZ, sizeX, sizeY, sizeZ, (int) product);
    }

    static Iterator<BlockPos> borrowIterator(int startX, int startY, int startZ, int sizeX, int sizeY, int sizeZ, int total) {
        Local local = local();
        PooledBlockPosIterator iterator = local.iterators.borrow();
        if (iterator == null) {
            local.iteratorOverflows++;
            return null;
        }
        iterator.reset(startX, startY, startZ, sizeX, sizeY, sizeZ, total);
        local.iteratorReuses++;
        return iterator;
    }

    static void releaseIterator(PooledBlockPosIterator iterator) {
        if (iterator == null || !iterator.takeCheckout()) {
            return;
        }
        local().iterators.release(iterator.poolIndex());
    }

    public static long hits() {
        return sum(Kind.HITS);
    }

    public static long misses() {
        return sum(Kind.MISSES);
    }

    public static long overflows() {
        return sum(Kind.OVERFLOWS);
    }

    private static long sum(Kind kind) {
        ThreadStates<Local> current = states;
        if (current == null) {
            return 0L;
        }
        long total = 0L;
        for (int i = 0; i < current.size(); i++) {
            Local local = current.at(i);
            if (kind == Kind.HITS) {
                total += local.positions.hits() + local.iteratorReuses;
            } else if (kind == Kind.MISSES) {
                total += local.positions.misses();
            } else {
                total += local.iteratorOverflows;
            }
        }
        return total;
    }

    private static Local local() {
        ThreadStates<Local> current = states;
        if (current == null) {
            synchronized (BlockPosPools.class) {
                current = states;
                if (current == null) {
                    current = new ThreadStates<>(Local::new);
                    states = current;
                }
            }
        }
        return current.get();
    }

    private enum Kind {
        HITS,
        MISSES,
        OVERFLOWS
    }

    private static final class Local {
        private final Int3Cache<BlockPos> positions;
        private final IndexedPool<PooledBlockPosIterator> iterators;
        private long iteratorReuses;
        private long iteratorOverflows;

        private Local() {
            ZeroChurnConfig config = ZeroChurnConfigLoader.get();
            this.positions = new Int3Cache<>(config.blockPosCacheSize);
            this.iterators = new IndexedPool<>(config.blockPosPoolCapacity, PooledBlockPosIterator::new);
        }
    }
}
