package dev.zerochurn.compat;

/**
 * Only known-safe particle class simple names may enter a pool. Modded subclasses
 * carry custom fields that a generic reset cannot reconstruct, so they always
 * allocate. Phase 3 mixins must call {@link #isPoolable(Class)} before offer/borrow.
 */
public final class ParticleWhitelist {
    private static final String[] ALLOWED = {
        "RainSplashParticle",
        "WaterBubbleParticle",
        "FlameParticle",
        "SmokeParticle",
        "TorchFlameParticle",
        "AshParticle"
    };

    private ParticleWhitelist() {
    }

    public static boolean isPoolable(Class<?> type) {
        if (type == null) {
            return false;
        }
        String name = type.getSimpleName();
        for (int i = 0; i < ALLOWED.length; i++) {
            if (ALLOWED[i].equals(name)) {
                // Reject anonymous / synthetic / clearly mod-prefixed packages later if needed.
                String packageName = type.getPackageName();
                return packageName.startsWith("net.minecraft.");
            }
        }
        return false;
    }
}
