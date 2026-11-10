package dev.zerochurn.mixin;

import dev.zerochurn.ZeroChurn;
import dev.zerochurn.compat.ModCompat;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Gates ZeroChurn mixins against other performance mods. Naming convention for
 * future mixins (Phase 2+): put the subsystem in the simple class name so this
 * plugin can disable the whole family without hard-coding every target.
 *
 * <ul>
 *   <li>Lithium — {@code *VoxelShape*}, {@code *FluidState*}, {@code *Collision*}, {@code *Pathfind*}</li>
 *   <li>Sodium — {@code *ChunkMesh*}, {@code *Vertex*}, {@code *BufferBuilder*}, {@code *WorldRenderer*}</li>
 *   <li>ImmediatelyFast — {@code *HudBatch*}, {@code *ParticleBatch*}, {@code *Immediate*}</li>
 *   <li>FerriteCore — {@code *BlockStateCache*}, {@code *Property*}</li>
 *   <li>ModernFix — {@code *DynamicResource*}, {@code *ModernFix*}</li>
 * </ul>
 */
public final class ZeroChurnMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        ZeroChurnConfigLoader.load();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String simple = simpleName(mixinClassName);

        if (ModCompat.isLithiumLoaded() && isLithiumOwned(simple)) {
            ZeroChurn.LOGGER.debug("Skipping {} — Lithium owns this hot path", simple);
            return false;
        }
        if (ModCompat.isSodiumLoaded() && isSodiumOwned(simple)) {
            ZeroChurn.LOGGER.debug("Skipping {} — Sodium owns this render path", simple);
            return false;
        }
        if (ModCompat.isImmediatelyFastLoaded() && isImmediatelyFastOwned(simple)) {
            ZeroChurn.LOGGER.debug("Skipping {} — ImmediatelyFast owns this batching path", simple);
            return false;
        }
        if (ModCompat.isFerriteCoreLoaded() && isFerriteCoreOwned(simple)) {
            ZeroChurn.LOGGER.debug("Skipping {} — FerriteCore owns this cache", simple);
            return false;
        }
        if (ModCompat.isModernFixLoaded() && isModernFixOwned(simple)) {
            ZeroChurn.LOGGER.debug("Skipping {} — ModernFix owns this path", simple);
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static String simpleName(String mixinClassName) {
        int slash = mixinClassName.lastIndexOf('.');
        return slash >= 0 ? mixinClassName.substring(slash + 1) : mixinClassName;
    }

    private static boolean isLithiumOwned(String simple) {
        return contains(simple, "VoxelShape")
            || contains(simple, "FluidState")
            || contains(simple, "Collision")
            || contains(simple, "Pathfind")
            || contains(simple, "EntityMove");
    }

    private static boolean isSodiumOwned(String simple) {
        return contains(simple, "ChunkMesh")
            || contains(simple, "Vertex")
            || contains(simple, "BufferBuilder")
            || contains(simple, "WorldRenderer")
            || contains(simple, "RenderRegion")
            || contains(simple, "TerrainBuild");
    }

    private static boolean isImmediatelyFastOwned(String simple) {
        return contains(simple, "HudBatch")
            || contains(simple, "ParticleBatch")
            || contains(simple, "ImmediateRender")
            || contains(simple, "HudRender");
    }

    private static boolean isFerriteCoreOwned(String simple) {
        return contains(simple, "BlockStateCache")
            || contains(simple, "PropertyCache")
            || contains(simple, "Ferrite");
    }

    private static boolean isModernFixOwned(String simple) {
        return contains(simple, "DynamicResource")
            || contains(simple, "ModernFix");
    }

    private static boolean contains(String haystack, String needle) {
        return haystack.indexOf(needle) >= 0;
    }
}
