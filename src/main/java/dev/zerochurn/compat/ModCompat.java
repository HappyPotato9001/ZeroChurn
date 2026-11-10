package dev.zerochurn.compat;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Cached presence checks for major Fabric performance mods. Queried from the
 * mixin config plugin (before most entrypoints) and from the startup log.
 */
public final class ModCompat {
    private static final boolean LITHIUM = FabricLoader.getInstance().isModLoaded("lithium");
    private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final boolean IMMEDIATELY_FAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
    private static final boolean FERRITE_CORE = FabricLoader.getInstance().isModLoaded("ferritecore");
    private static final boolean MODERN_FIX = FabricLoader.getInstance().isModLoaded("modernfix");
    private static final boolean C2ME = FabricLoader.getInstance().isModLoaded("c2me");

    private ModCompat() {
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

    public static boolean isC2meLoaded() {
        return C2ME;
    }
}
