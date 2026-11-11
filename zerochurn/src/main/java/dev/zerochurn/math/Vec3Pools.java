package dev.zerochurn.math;

import dev.zerochurn.config.ZeroChurnConfig;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.pool.Int3Cache;
import dev.zerochurn.pool.ThreadStates;
import net.minecraft.world.phys.Vec3;

/**
 * Interns common integer-keyed {@link Vec3} factories. Values are immutable, so
 * cache hits are safe to store anywhere.
 */
public final class Vec3Pools {
    public static final long BYTES_PER_HIT = 40L;

    private static volatile ThreadStates<Local> states;

    private Vec3Pools() {
    }

    public static boolean enabled() {
        return ZeroChurnConfigLoader.get().poolVec3d;
    }

    public static Vec3 getOrigin(int x, int y, int z) {
        return enabled() ? local().origin.get(x, y, z) : null;
    }

    public static void storeOrigin(int x, int y, int z, Vec3 value) {
        if (enabled() && value != null) {
            local().origin.put(x, y, z, value);
        }
    }

    public static Vec3 getCenter(int x, int y, int z) {
        return enabled() ? local().center.get(x, y, z) : null;
    }

    public static void storeCenter(int x, int y, int z, Vec3 value) {
        if (enabled() && value != null) {
            local().center.put(x, y, z, value);
        }
    }

    public static Vec3 getBottomCenter(int x, int y, int z) {
        return enabled() ? local().bottom.get(x, y, z) : null;
    }

    public static void storeBottomCenter(int x, int y, int z, Vec3 value) {
        if (enabled() && value != null) {
            local().bottom.put(x, y, z, value);
        }
    }

    public static boolean isExactInt(double value) {
        return value == Math.rint(value) && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
    }

    public static boolean isHalf(double value) {
        return value - Math.floor(value) == 0.5d;
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
            if (hits) {
                total += local.origin.hits() + local.center.hits() + local.bottom.hits();
            } else {
                total += local.origin.misses() + local.center.misses() + local.bottom.misses();
            }
        }
        return total;
    }

    private static Local local() {
        ThreadStates<Local> current = states;
        if (current == null) {
            synchronized (Vec3Pools.class) {
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
        private final Int3Cache<Vec3> origin;
        private final Int3Cache<Vec3> center;
        private final Int3Cache<Vec3> bottom;

        private Local() {
            ZeroChurnConfig config = ZeroChurnConfigLoader.get();
            this.origin = new Int3Cache<>(config.vec3dCacheSize);
            this.center = new Int3Cache<>(config.vec3dCacheSize);
            this.bottom = new Int3Cache<>(config.vec3dCacheSize);
        }
    }
}
