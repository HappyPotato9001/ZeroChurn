package dev.zerochurn.mixin;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroChurnMixinPluginTest {
    @Test
    void lithiumGateMatchesCollisionFamilyNames() throws Exception {
        Method lithium = ZeroChurnMixinPlugin.class.getDeclaredMethod("isLithiumOwned", String.class);
        lithium.setAccessible(true);
        assertTrue((Boolean) lithium.invoke(null, "VoxelShapesCollisionMixin"));
        assertTrue((Boolean) lithium.invoke(null, "FluidStateFlowMixin"));
        assertTrue((Boolean) lithium.invoke(null, "EntityMovePoolingMixin"));
        assertFalse((Boolean) lithium.invoke(null, "BlockPosFloorMixin"));
    }

    @Test
    void sodiumGateMatchesRenderFamilyNames() throws Exception {
        Method sodium = ZeroChurnMixinPlugin.class.getDeclaredMethod("isSodiumOwned", String.class);
        sodium.setAccessible(true);
        assertTrue((Boolean) sodium.invoke(null, "ChunkMeshBufferMixin"));
        assertTrue((Boolean) sodium.invoke(null, "WorldRendererPoolMixin"));
        assertFalse((Boolean) sodium.invoke(null, "Vec3dInternMixin"));
    }
}
