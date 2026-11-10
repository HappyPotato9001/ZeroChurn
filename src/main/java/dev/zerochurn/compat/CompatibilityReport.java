package dev.zerochurn.compat;

import dev.zerochurn.ZeroChurn;
import dev.zerochurn.config.ZeroChurnConfig;
import dev.zerochurn.config.ZeroChurnConfigLoader;

/**
 * Prints which ZeroChurn modules stay active versus which are yielded to
 * another performance mod. Safe to call from both common and client init.
 */
public final class CompatibilityReport {
    private CompatibilityReport() {
    }

    public static void log() {
        ZeroChurnConfig config = ZeroChurnConfigLoader.get();

        ZeroChurn.LOGGER.info(
            "ZeroChurn modules | blockPos={} vec3d={} aabb={} particles={} debug={}",
            enabled(config.poolBlockPos),
            enabled(config.poolVec3d),
            enabled(config.poolAABB),
            enabled(config.poolParticles),
            enabled(config.enableDebugStats)
        );

        if (ModCompat.isLithiumLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] Lithium detected: yielding collision, VoxelShapes, FluidState, and pathfinding pooling"
            );
        } else {
            ZeroChurn.LOGGER.info("[ZeroChurn] Lithium not present: collision/fluid pooling allowed in Phase 2");
        }

        if (ModCompat.isSodiumLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] Sodium detected: yielding chunk-meshing and render vertex/buffer pooling"
            );
        } else {
            ZeroChurn.LOGGER.info("[ZeroChurn] Sodium not present: render-thread buffer pooling allowed if added later");
        }

        if (ModCompat.isImmediatelyFastLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] ImmediatelyFast detected: yielding HUD/particle batching hooks"
            );
        } else {
            ZeroChurn.LOGGER.info("[ZeroChurn] ImmediatelyFast not present: HUD/particle batch hooks allowed if added later");
        }

        if (ModCompat.isFerriteCoreLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] FerriteCore detected: yielding BlockState/property cache mixins"
            );
        }

        if (ModCompat.isModernFixLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] ModernFix detected: yielding dynamic-resource / ModernFix-owned paths"
            );
        }

        if (ModCompat.isC2meLoaded()) {
            ZeroChurn.LOGGER.info(
                "[ZeroChurn] C2ME detected: ThreadLocal pools stay per-worker; use ThreadLocalPool.discardThreadState() on custom worker teardown"
            );
        }

        ZeroChurn.LOGGER.info(
            "[ZeroChurn] Mixin policy: MixinExtras only, priority 1100, defaultRequire 0 — wrap around other mods, never overwrite"
        );
    }

    private static String enabled(boolean on) {
        return on ? "on" : "off";
    }
}
