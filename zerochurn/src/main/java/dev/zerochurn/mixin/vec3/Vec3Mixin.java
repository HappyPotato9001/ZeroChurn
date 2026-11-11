package dev.zerochurn.mixin.vec3;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.math.Vec3Pools;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Vec3.class)
public class Vec3Mixin {
    @WrapOperation(
        method = "atLowerCornerOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;",
        at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"),
        require = 0
    )
    private static Vec3 zerochurn$origin(double x, double y, double z, Operation<Vec3> original) {
        if (!ZeroChurnConfigLoader.get().poolVec3d || !Vec3Pools.isExactInt(x) || !Vec3Pools.isExactInt(y) || !Vec3Pools.isExactInt(z)) {
            return original.call(x, y, z);
        }
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        Vec3 cached = Vec3Pools.getOrigin(ix, iy, iz);
        if (cached != null) {
            return cached;
        }
        Vec3 created = original.call(x, y, z);
        Vec3Pools.storeOrigin(ix, iy, iz, created);
        return created;
    }

    @WrapOperation(
        method = "atLowerCornerWithOffset(Lnet/minecraft/core/Vec3i;DDD)Lnet/minecraft/world/phys/Vec3;",
        at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"),
        require = 0
    )
    private static Vec3 zerochurn$offset(double x, double y, double z, Operation<Vec3> original) {
        if (!ZeroChurnConfigLoader.get().poolVec3d) {
            return original.call(x, y, z);
        }
        if (Vec3Pools.isHalf(x) && Vec3Pools.isHalf(y) && Vec3Pools.isHalf(z)) {
            int ix = (int) Math.floor(x);
            int iy = (int) Math.floor(y);
            int iz = (int) Math.floor(z);
            Vec3 cached = Vec3Pools.getCenter(ix, iy, iz);
            if (cached != null) {
                return cached;
            }
            Vec3 created = original.call(x, y, z);
            Vec3Pools.storeCenter(ix, iy, iz, created);
            return created;
        }
        if (Vec3Pools.isHalf(x) && Vec3Pools.isExactInt(y) && Vec3Pools.isHalf(z)) {
            int ix = (int) Math.floor(x);
            int iy = (int) y;
            int iz = (int) Math.floor(z);
            Vec3 cached = Vec3Pools.getBottomCenter(ix, iy, iz);
            if (cached != null) {
                return cached;
            }
            Vec3 created = original.call(x, y, z);
            Vec3Pools.storeBottomCenter(ix, iy, iz, created);
            return created;
        }
        return original.call(x, y, z);
    }
}
