package dev.zerochurn.metrics;

import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.math.AabbPools;
import dev.zerochurn.math.BlockPosPools;
import dev.zerochurn.math.Vec3Pools;

/**
 * Aggregates hit/miss counters from every pooling subsystem.
 * {@link #summary()} samples rates; {@link #hudLine()} is allocation-light and
 * safe to poll every frame from InfoForge.
 */
public final class ZeroChurnStats {
    private static long lastNanos = System.nanoTime();
    private static long lastHits;
    private static long lastBytes;
    private static long cachedRateHitsPerSec;
    private static long cachedRateBytesPerSec;
    private static long lastHudRefreshNanos;
    private static String cachedHudLine = "ZeroChurn …";

    private ZeroChurnStats() {
    }

    /**
     * Compact companion HUD string. Refreshes at most ~4 times/sec and never
     * requires enableDebugStats.
     */
    public static String hudLine() {
        long now = System.nanoTime();
        if (now - lastHudRefreshNanos < 250_000_000L && cachedHudLine != null) {
            return cachedHudLine;
        }
        lastHudRefreshNanos = now;

        long blockHits = BlockPosPools.hits();
        long vecHits = Vec3Pools.hits();
        long aabbHits = AabbPools.hits();
        long hits = blockHits + vecHits + aabbHits;
        long misses = BlockPosPools.misses() + Vec3Pools.misses() + AabbPools.misses();
        long total = hits + misses;
        int rate = total <= 0L ? 0 : (int) ((hits * 100L) / total);

        StringBuilder out = new StringBuilder(72);
        out.append("ZC ");
        out.append(rate);
        out.append("%  ");
        out.append(formatCompact(hits));
        out.append(" hits  b/v/a ");
        out.append(formatCompact(blockHits));
        out.append('/');
        out.append(formatCompact(vecHits));
        out.append('/');
        out.append(formatCompact(aabbHits));
        if (cachedRateBytesPerSec > 0L) {
            out.append("  ~");
            out.append(formatBytes(cachedRateBytesPerSec));
            out.append("/s");
        }
        cachedHudLine = out.toString();
        return cachedHudLine;
    }

    public static String summary() {
        long blockHits = BlockPosPools.hits();
        long vecHits = Vec3Pools.hits();
        long aabbHits = AabbPools.hits();
        long totalHits = blockHits + vecHits + aabbHits;
        long totalMisses = BlockPosPools.misses() + Vec3Pools.misses() + AabbPools.misses();
        long totalBytes = blockHits * BlockPosPools.BYTES_PER_POSITION
            + vecHits * Vec3Pools.BYTES_PER_HIT
            + aabbHits * AabbPools.BYTES_PER_HIT;

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

        lastNanos = now;
        lastHits = totalHits;
        lastBytes = totalBytes;
        cachedRateHitsPerSec = Math.round(deltaHits / seconds);
        cachedRateBytesPerSec = Math.round(deltaBytes / seconds);

        long lookedUp = totalHits + totalMisses;
        int hitRate = lookedUp <= 0L ? 0 : (int) ((totalHits * 100L) / lookedUp);

        StringBuilder out = new StringBuilder(260);
        if (!ZeroChurnConfigLoader.get().enableDebugStats) {
            out.append("ZeroChurn ");
            out.append(hitRate);
            out.append("% hit rate (");
            out.append(formatCompact(totalHits));
            out.append(" hits). Tip: set enableDebugStats=true for live /s rates.");
            return out.toString();
        }

        out.append("ZeroChurn ~");
        out.append(formatBytes(cachedRateBytesPerSec));
        out.append("/s saved (");
        out.append(cachedRateHitsPerSec);
        out.append(" hits/s)  ");
        out.append(hitRate);
        out.append("% hit | blockPos ");
        out.append(blockHits);
        out.append('/');
        out.append(BlockPosPools.misses());
        out.append('/');
        out.append(BlockPosPools.overflows());
        out.append(" | vec3 ");
        out.append(vecHits);
        out.append('/');
        out.append(Vec3Pools.misses());
        out.append(" | aabb ");
        out.append(aabbHits);
        out.append('/');
        out.append(AabbPools.misses());
        out.append(" (hit/miss[/overflow])");
        return out.toString();
    }

    public static String statusLine() {
        var config = ZeroChurnConfigLoader.get();
        StringBuilder out = new StringBuilder(120);
        out.append("ZeroChurn modules: blockPos=");
        out.append(on(config.poolBlockPos));
        out.append(" vec3=");
        out.append(on(config.poolVec3d));
        out.append(" aabb=");
        out.append(on(config.poolAABB));
        out.append(" particles=");
        out.append(on(config.poolParticles));
        out.append(" debug=");
        out.append(on(config.enableDebugStats));
        return out.toString();
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

    private static String formatCompact(long value) {
        if (value < 1000L) {
            return Long.toString(value);
        }
        if (value < 1_000_000L) {
            return (value / 1000L) + "k";
        }
        return (value / 1_000_000L) + "m";
    }

    private static String on(boolean value) {
        return value ? "on" : "off";
    }
}
