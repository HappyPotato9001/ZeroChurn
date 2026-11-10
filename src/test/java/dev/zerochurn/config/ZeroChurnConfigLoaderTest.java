package dev.zerochurn.config;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroChurnConfigLoaderTest {
    @Test
    void gsonRoundTripsToggles() {
        ZeroChurnConfig config = new ZeroChurnConfig();
        config.poolBlockPos = false;
        config.poolVec3d = true;
        config.poolAABB = false;
        config.poolParticles = true;
        config.enableDebugStats = true;
        config.blockPosPoolCapacity = 100;

        Gson gson = new Gson();
        ZeroChurnConfig parsed = gson.fromJson(gson.toJson(config), ZeroChurnConfig.class);
        parsed.sanitize();

        assertFalse(parsed.poolBlockPos);
        assertTrue(parsed.poolVec3d);
        assertFalse(parsed.poolAABB);
        assertTrue(parsed.poolParticles);
        assertTrue(parsed.enableDebugStats);
        assertEquals(100, parsed.blockPosPoolCapacity);
    }
}
