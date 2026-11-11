package dev.infoforge.compat;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Soft presence checks only. InfoForge never hard-depends on performance mods
 * and never touches their internals.
 */
public final class ModCompat {
    private static final boolean ZEROCHURN = FabricLoader.getInstance().isModLoaded("zerochurn");
    private static final boolean LITHIUM = FabricLoader.getInstance().isModLoaded("lithium");
    private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final boolean IMMEDIATELY_FAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
    private static final boolean FERRITE_CORE = FabricLoader.getInstance().isModLoaded("ferritecore");
    private static final boolean MODERN_FIX = FabricLoader.getInstance().isModLoaded("modernfix");
    private static final boolean IRIS = FabricLoader.getInstance().isModLoaded("iris");
    private static final boolean CONTINUITY = FabricLoader.getInstance().isModLoaded("continuity");
    private static final boolean ENTITY_CULLING = FabricLoader.getInstance().isModLoaded("entityculling");

    private ModCompat() {
    }

    public static boolean isZeroChurnLoaded() {
        return ZEROCHURN;
    }

    public static boolean isLithiumLoaded() {
        return LITHIUM;
    }

    public static boolean isSodiumLoaded() {
        return SODIUM;
    }

    public static boolean isImmediatelyFastLoaded() {
        return IMMEDIATELY_FAST;
    }

    public static boolean isFerriteCoreLoaded() {
        return FERRITE_CORE;
    }

    public static boolean isModernFixLoaded() {
        return MODERN_FIX;
    }

    public static boolean isIrisLoaded() {
        return IRIS;
    }

    public static boolean isContinuityLoaded() {
        return CONTINUITY;
    }

    public static boolean isEntityCullingLoaded() {
        return ENTITY_CULLING;
    }

    public static String companionStatusLine() {
        if (ZEROCHURN) {
            return "Companion ready · ZeroChurn linked";
        }
        return "Standalone mode · ZeroChurn optional";
    }
}
