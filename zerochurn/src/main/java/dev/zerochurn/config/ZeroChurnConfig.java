package dev.zerochurn.config;

import dev.zerochurn.pool.ThreadLocalPool;

/**
 * Flat Gson-backed toggles for every pooling subsystem. Booleans are read on
 * the hot path after a reload. Capacities/cache sizes are fixed at first use.
 */
public final class ZeroChurnConfig {
    public boolean poolBlockPos = true;
    public boolean poolVec3d = true;
    public boolean poolAABB = true;
    public boolean poolParticles = true;
    public boolean enableDebugStats = false;

    public int blockPosPoolCapacity = 64;
    public int blockPosCacheSize = 2048;
    public int vec3dPoolCapacity = 64;
    public int vec3dCacheSize = 1024;
    public int aabbPoolCapacity = 32;
    public int aabbCacheSize = 2048;
    public int particlePoolCapacity = 256;

    public void copyFrom(ZeroChurnConfig other) {
        this.poolBlockPos = other.poolBlockPos;
        this.poolVec3d = other.poolVec3d;
        this.poolAABB = other.poolAABB;
        this.poolParticles = other.poolParticles;
        this.enableDebugStats = other.enableDebugStats;
        this.blockPosPoolCapacity = other.blockPosPoolCapacity;
        this.blockPosCacheSize = other.blockPosCacheSize;
        this.vec3dPoolCapacity = other.vec3dPoolCapacity;
        this.vec3dCacheSize = other.vec3dCacheSize;
        this.aabbPoolCapacity = other.aabbPoolCapacity;
        this.aabbCacheSize = other.aabbCacheSize;
        this.particlePoolCapacity = other.particlePoolCapacity;
    }

    public void sanitize() {
        this.blockPosPoolCapacity = clamp(this.blockPosPoolCapacity, 1, 4096);
        this.vec3dPoolCapacity = clamp(this.vec3dPoolCapacity, 1, 4096);
        this.aabbPoolCapacity = clamp(this.aabbPoolCapacity, 1, 4096);
        this.particlePoolCapacity = clamp(this.particlePoolCapacity, 1, 4096);
        this.blockPosCacheSize = ThreadLocalPool.nextPowerOfTwo(clamp(this.blockPosCacheSize, 16, 16384));
        this.vec3dCacheSize = ThreadLocalPool.nextPowerOfTwo(clamp(this.vec3dCacheSize, 16, 16384));
        this.aabbCacheSize = ThreadLocalPool.nextPowerOfTwo(clamp(this.aabbCacheSize, 16, 16384));
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
