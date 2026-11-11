package dev.zerochurn.mixin.aabb;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.math.AabbPools;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AABB.class)
public class AABBMixin {
    @WrapOperation(
        method = {
            "move(DDD)Lnet/minecraft/world/phys/AABB;",
            "encapsulatingFullBlocks(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
        },
        at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"),
        require = 0
    )
    private static AABB zerochurn$unit(
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ,
        Operation<AABB> original
    ) {
        if (!ZeroChurnConfigLoader.get().poolAABB || !AabbPools.isUnitCube(minX, minY, minZ, maxX, maxY, maxZ)) {
            return original.call(minX, minY, minZ, maxX, maxY, maxZ);
        }
        int x = (int) minX;
        int y = (int) minY;
        int z = (int) minZ;
        AABB cached = AabbPools.getUnit(x, y, z);
        if (cached != null) {
            return cached;
        }
        AABB created = original.call(minX, minY, minZ, maxX, maxY, maxZ);
        AabbPools.storeUnit(x, y, z, created);
        return created;
    }
}
