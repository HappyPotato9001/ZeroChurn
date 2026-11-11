package dev.zerochurn.mixin.blockpos;

import dev.zerochurn.math.BlockPosPools;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lithium already replaces BlockPos iteration. The mixin plugin skips this class
 * when Lithium is present so the two mods compose cleanly.
 */
@Mixin(BlockPos.class)
public class BlockPosBetweenClosedMixin {
    @Inject(
        method = "betweenClosed(IIIIII)Ljava/lang/Iterable;",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void zerochurn$betweenClosed(
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        CallbackInfoReturnable<Iterable<BlockPos>> cir
    ) {
        Iterable<BlockPos> pooled = BlockPosPools.tryBetweenClosed(startX, startY, startZ, endX, endY, endZ);
        if (pooled != null) {
            cir.setReturnValue(pooled);
        }
    }
}
