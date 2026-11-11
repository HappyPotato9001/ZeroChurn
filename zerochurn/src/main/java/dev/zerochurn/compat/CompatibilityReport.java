package dev.zerochurn.compat;

import dev.zerochurn.ZeroChurn;
import dev.zerochurn.config.ZeroChurnConfig;
import dev.zerochurn.config.ZeroChurnConfigLoader;

/**
 * Prints which ZeroChurn modules stay active versus which are yielded to
 * another performance mod.
 */
public final class CompatibilityReport {
    private CompatibilityReport() {
    }

    public static void log() {
        ZeroChurnConfig config = ZeroChurnConfigLoader.get();

        ZeroChurn.LOGGER.info(
            "ZeroChurn active | blockPos={} vec3={} aabb={} particles={} debug={}",
            on(config.poolBlockPos),
            on(config.poolVec3d),
            on(config.poolAABB),
            on(config.poolParticles),
            on(config.enableDebugStats)
        );

        if (ModCompat.isLithiumLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] Lithium detected: yielding BlockPos.betweenClosed, collision, VoxelShapes, FluidState, and pathfinding"
            );
        }
        if (ModCompat.isSodiumLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] Sodium detected: yielding chunk-meshing / render vertex-buffer pooling"
            );
        }
        if (ModCompat.isImmediatelyFastLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] ImmediatelyFast detected: yielding HUD/particle batching hooks"
            );
        }
        if (ModCompat.isFerriteCoreLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] FerriteCore detected: yielding BlockState/property cache mixins"
            );
        }
        if (ModCompat.isModernFixLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] ModernFix detected: yielding ModernFix-owned paths"
            );
        }
        if (ModCompat.isC2meLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] C2ME detected: pools stay per-worker; discardThreadState() on worker teardown"
            );
        }
        if (ModCompat.isInfoForgeLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] InfoForge detected: companion HUD may read stats via reflection; pooling unchanged"
            );
        }

        ZeroChurn.LOGGER.info(
            "[ZeroChurn] Hot paths: BlockPos.containing intern, Vec3 corner/center intern, AABB unit-cube intern"
            + (ModCompat.isLithiumLoaded() ? "" : ", BlockPos.betweenClosed iterator pool")
        );
    }

    private static String on(boolean value) {
        return value ? "on" : "off";
    }
}
