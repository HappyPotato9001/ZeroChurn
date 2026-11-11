package dev.infoforge.compat;

import dev.infoforge.InfoForge;

/**
 * Startup compatibility notes. InfoForge stays on Fabric HUD APIs and avoids
 * world-renderer / ImmediatelyFast batching mixins.
 */
public final class CompatibilityReport {
    private CompatibilityReport() {
    }

    public static void log() {
        InfoForge.LOGGER.info(
            "InfoForge compat | zerochurn={} sodium={} iris={} immediatelyfast={} lithium={} ferritecore={} modernfix={}",
            ModCompat.isZeroChurnLoaded(),
            ModCompat.isSodiumLoaded(),
            ModCompat.isIrisLoaded(),
            ModCompat.isImmediatelyFastLoaded(),
            ModCompat.isLithiumLoaded(),
            ModCompat.isFerriteCoreLoaded(),
            ModCompat.isModernFixLoaded()
        );

        if (ModCompat.isZeroChurnLoaded()) {
            InfoForge.LOGGER.info("[InfoForge] ZeroChurn detected: optional stats line available (read-only)");
        }
        if (ModCompat.isSodiumLoaded()) {
            InfoForge.LOGGER.info("[InfoForge] Sodium detected: no world-renderer mixins; map sampling stays client-safe");
        }
        if (ModCompat.isImmediatelyFastLoaded()) {
            InfoForge.LOGGER.info("[InfoForge] ImmediatelyFast detected: HUD uses HudElementRegistry only");
        }
        if (ModCompat.isIrisLoaded()) {
            InfoForge.LOGGER.info("[InfoForge] Iris detected: overlay stays in Fabric HUD pass");
        }
        if (ModCompat.isLithiumLoaded() || ModCompat.isFerriteCoreLoaded() || ModCompat.isModernFixLoaded()) {
            InfoForge.LOGGER.info("[InfoForge] Server/common perf mods present: InfoForge remains client-HUD only");
        }
    }
}
