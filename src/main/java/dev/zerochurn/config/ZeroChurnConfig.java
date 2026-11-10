package dev.zerochurn.config;

/**
 * Flat Gson-backed toggles for every pooling subsystem. Booleans are read on
 * the hot path after a reload. Pool capacities are fixed when a pool is first
 * constructed.
 */
public final class ZeroChurnConfig {
    public boolean poolBlockPos = true;
    public boolean poolVec3d = true;
    public boolean poolAABB = true;
    public boolean poolParticles = true;
    public boolean enableDebugStats = false;

    public int blockPosPoolCapacity = 64;
    public int vec3dPoolCapacity = 64;
    public int aabbPoolCapacity = 32;
    public int particlePoolCapacity = 256;

    public void copyFrom(ZeroChurnConfig other) {
        this.poolBlockPos = other.poolBlockPos;
        this.poolVec3d = other.poolVec3d;
        this.poolAABB = other.poolAABB;
        this.poolParticles = other.poolParticles;
        this.enableDebugStats = other.enableDebugStats;
        this.blockPosPoolCapacity = other.blockPosPoolCapacity;
        this.vec3dPoolCapacity = other.vec3dPoolCapacity;
        this.aabbPoolCapacity = other.aabbPoolCapacity;
        this.particlePoolCapacity = other.particlePoolCapacity;
    }

    public void sanitize() {
        this.blockPosPoolCapacity = clamp(this.blockPosPoolCapacity, 1, 4096);
        this.vec3dPoolCapacity = clamp(this.vec3dPoolCapacity, 1, 4096);
        this.aabbPoolCapacity = clamp(this.aabbPoolCapacity, 1, 4096);
        this.particlePoolCapacity = clamp(this.particlePoolCapacity, 1, 4096);
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
