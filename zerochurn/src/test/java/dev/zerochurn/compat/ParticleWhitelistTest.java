package dev.zerochurn.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ParticleWhitelistTest {
    @Test
    void allowsExactVanillaSimpleNamesOnlyViaPackageGuard() {
        // Simulated by local classes that share names would fail the package check.
        assertFalse(ParticleWhitelist.isPoolable(RainSplashParticle.class));
        assertFalse(ParticleWhitelist.isPoolable(Object.class));
        assertFalse(ParticleWhitelist.isPoolable(null));
    }

    @Test
    void rejectsUnknownTypes() {
        assertFalse(ParticleWhitelist.isPoolable(String.class));
    }

    /**
     * Stand-in that shares a whitelisted simple name but lives outside net.minecraft.
     */
    private static final class RainSplashParticle {
    }
}
