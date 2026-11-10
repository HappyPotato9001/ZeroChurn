package dev.zerochurn.metrics;

import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.pool.ThreadLocalPool;

/**
 * Hit/miss counters for every pool. Counters are primitive longs so the hot
 * path never boxes. {@link #summary()} builds a short chat string with estimated
 * allocation savings per second since the previous sample.
 */
public final class ZeroChurnStats {
    public static final long BYTES_BLOCK_POS = 32L;
    public static final long BYTES_VEC3D = 40L;
    public static final long BYTES_AABB = 64L;
    public static final long BYTES_PARTICLE = 192L;

    private static final ThreadLocalPool.Snapshot EMPTY = new ThreadLocalPool.Snapshot(0L, 0L);

    private static volatile ThreadLocalPool<?> blockPosPool;
    private static volatile ThreadLocalPool<?> vec3dPool;
    private static volatile ThreadLocalPool<?> aabbPool;
    private static volatile ThreadLocalPool<?> particlePool;

    private static long lastNanos = System.nanoTime();
    private static long lastHits;
    private static long lastBytes;

    private ZeroChurnStats() {
    }

    public static void bindBlockPos(ThreadLocalPool<?> pool) {
        blockPosPool = pool;
    }

    public static void bindVec3d(ThreadLocalPool<?> pool) {
        vec3dPool = pool;
    }

    public static void bindAabb(ThreadLocalPool<?> pool) {
        aabbPool = pool;
    }

    public static void bindParticle(ThreadLocalPool<?> pool) {
        particlePool = pool;
    }

    public static String summary() {
        if (!ZeroChurnConfigLoader.get().enableDebugStats) {
            return "ZeroChurn debug stats are disabled (enableDebugStats=false).";
        }

        long blockHits = hits(blockPosPool);
        long vecHits = hits(vec3dPool);
        long aabbHits = hits(aabbPool);
        long particleHits = hits(particlePool);
        long totalHits = blockHits + vecHits + aabbHits + particleHits;
        long totalBytes = blockHits * BYTES_BLOCK_POS
            + vecHits * BYTES_VEC3D
            + aabbHits * BYTES_AABB
            + particleHits * BYTES_PARTICLE;

        long now = System.nanoTime();
        long elapsedNanos = now - lastNanos;
        if (elapsedNanos < 1L) {
            elapsedNanos = 1L;
        }
        double seconds = elapsedNanos / 1_000_000_000.0;
        long deltaHits = totalHits - lastHits;
        long deltaBytes = totalBytes - lastBytes;
        if (deltaHits < 0L) {
            deltaHits = totalHits;
        }
        if (deltaBytes < 0L) {
            deltaBytes = totalBytes;
        }
        double hitsPerSecond = deltaHits / seconds;
        double bytesPerSecond = deltaBytes / seconds;

        lastNanos = now;
        lastHits = totalHits;
        lastBytes = totalBytes;

        StringBuilder out = new StringBuilder(192);
        out.append("ZeroChurn ~");
        out.append(formatBytes((long) bytesPerSecond));
        out.append("/s saved (");
        appendRate(out, hitsPerSecond);
        out.append(" hits/s) | blockPos ");
        out.append(blockHits);
        out.append('/');
        out.append(misses(blockPosPool));
        out.append(" | vec3d ");
        out.append(vecHits);
        out.append('/');
        out.append(misses(vec3dPool));
        out.append(" | aabb ");
        out.append(aabbHits);
        out.append('/');
        out.append(misses(aabbPool));
        out.append(" | particles ");
        out.append(particleHits);
        out.append('/');
        out.append(misses(particlePool));
        out.append(" (hit/miss)");
        return out.toString();
    }

    private static long hits(ThreadLocalPool<?> pool) {
        return snapshot(pool).hits;
    }

    private static long misses(ThreadLocalPool<?> pool) {
        return snapshot(pool).misses;
    }

    private static ThreadLocalPool.Snapshot snapshot(ThreadLocalPool<?> pool) {
        if (pool == null) {
            return EMPTY;
        }
        return pool.snapshot();
    }

    private static void appendRate(StringBuilder out, double value) {
        long rounded = Math.round(value);
        out.append(rounded);
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }
        if (bytes < 1024L * 1024L) {
            return (bytes / 1024L) + " KB";
        }
        return (bytes / (1024L * 1024L)) + " MB";
    }
}
