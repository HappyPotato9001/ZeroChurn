package dev.zerochurn.mixin.blockpos;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.math.BlockPosPools;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPos.class)
public class BlockPosContainingMixin {
    @WrapOperation(
        method = "containing(DDD)Lnet/minecraft/core/BlockPos;",
        at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;"),
        require = 0
    )
    private static BlockPos zerochurn$containing(int x, int y, int z, Operation<BlockPos> original) {
        if (!ZeroChurnConfigLoader.get().poolBlockPos) {
            return original.call(x, y, z);
        }
        BlockPos cached = BlockPosPools.get(x, y, z);
        if (cached != null) {
            return cached;
        }
        BlockPos created = original.call(x, y, z);
        BlockPosPools.store(x, y, z, created);
        return created;
    }
}
