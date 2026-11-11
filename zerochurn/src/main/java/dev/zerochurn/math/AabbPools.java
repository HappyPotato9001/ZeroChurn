package dev.zerochurn.math;

import dev.zerochurn.config.ZeroChurnConfig;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.pool.Int3Cache;
import dev.zerochurn.pool.ThreadStates;
import net.minecraft.world.phys.AABB;

/**
 * Interns integer-aligned unit cubes. Arbitrary AABBs still allocate.
 */
public final class AabbPools {
    public static final long BYTES_PER_HIT = 64L;

    private static volatile ThreadStates<Local> states;

    private AabbPools() {
    }

    public static boolean enabled() {
        return ZeroChurnConfigLoader.get().poolAABB;
    }

    public static boolean isExactInt(double value) {
        return value == Math.rint(value) && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
    }

    public static boolean isUnitCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return isExactInt(minX)
            && isExactInt(minY)
            && isExactInt(minZ)
            && maxX - minX == 1.0d
            && maxY - minY == 1.0d
            && maxZ - minZ == 1.0d;
    }

    public static AABB getUnit(int x, int y, int z) {
        return enabled() ? local().units.get(x, y, z) : null;
    }

    public static void storeUnit(int x, int y, int z, AABB value) {
        if (enabled() && value != null) {
            local().units.put(x, y, z, value);
        }
    }

    public static long hits() {
        return sum(true);
    }

    public static long misses() {
        return sum(false);
    }

    private static long sum(boolean hits) {
        ThreadStates<Local> current = states;
        if (current == null) {
            return 0L;
        }
        long total = 0L;
        for (int i = 0; i < current.size(); i++) {
            Local local = current.at(i);
            total += hits ? local.units.hits() : local.units.misses();
        }
        return total;
    }

    private static Local local() {
        ThreadStates<Local> current = states;
        if (current == null) {
            synchronized (AabbPools.class) {
                current = states;
                if (current == null) {
                    current = new ThreadStates<>(Local::new);
                    states = current;
                }
            }
        }
        return current.get();
    }

    private static final class Local {
        private final Int3Cache<AABB> units;

        private Local() {
            ZeroChurnConfig config = ZeroChurnConfigLoader.get();
            this.units = new Int3Cache<>(config.aabbCacheSize);
        }
    }
}
