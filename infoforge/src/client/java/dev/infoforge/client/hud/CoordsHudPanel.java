package dev.infoforge.client.hud;

import dev.infoforge.config.InfoForgeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

final class CoordsHudPanel {
    private final StringBuilder xyz = new StringBuilder(56);
    private final StringBuilder chunk = new StringBuilder(32);
    private final StringBuilder speed = new StringBuilder(24);
    private final StringBuilder dim = new StringBuilder(32);
    private final StringBuilder biome = new StringBuilder(40);
    private final StringBuilder light = new StringBuilder(40);
    private final StringBuilder block = new StringBuilder(40);
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private boolean prepared;

    void prepare(Minecraft client, InfoForgeConfig config) {
        if (!config.showCoords || client.player == null || client.level == null) {
            this.prepared = false;
            return;
        }
        build(client, config);
        this.prepared = true;
    }

    int measure(Minecraft client, InfoForgeConfig config, Font font) {
        if (!config.showCoords || client.player == null || client.level == null) {
            return 0;
        }
        if (!this.prepared) {
            build(client, config);
        }
        int max = HudStyle.lineWidth(font, "XYZ  ", this.xyz.toString());
        if (config.showChunk) {
            max = Math.max(max, HudStyle.lineWidth(font, "CHK  ", this.chunk.toString()));
        }
        if (config.showSpeed) {
            max = Math.max(max, HudStyle.lineWidth(font, "SPD  ", this.speed.toString()));
        }
        if (config.showDimension) {
            max = Math.max(max, HudStyle.lineWidth(font, "DIM  ", this.dim.toString()));
        }
        if (config.showBiome) {
            max = Math.max(max, HudStyle.lineWidth(font, "BIO  ", this.biome.toString()));
        }
        if (config.showLight) {
            max = Math.max(max, HudStyle.lineWidth(font, "LIT  ", this.light.toString()));
        }
        if (config.showBlock) {
            max = Math.max(max, HudStyle.lineWidth(font, "BLK  ", this.block.toString()));
        }
        return max;
    }

    int lineCount(InfoForgeConfig config) {
        if (!config.showCoords) {
            return 0;
        }
        int lines = 1;
        if (config.showChunk) {
            lines++;
        }
        if (config.showSpeed) {
            lines++;
        }
        if (config.showDimension) {
            lines++;
        }
        if (config.showBiome) {
            lines++;
        }
        if (config.showLight) {
            lines++;
        }
        if (config.showBlock) {
            lines++;
        }
        return lines;
    }

    int render(GuiGraphicsExtractor graphics, Minecraft client, InfoForgeConfig config, int x, int y) {
        if (!config.showCoords || client.player == null || client.level == null) {
            return y;
        }
        if (!this.prepared) {
            build(client, config);
        }

        HudStyle.drawLabeled(graphics, client.font, "XYZ  ", this.xyz.toString(), x, y, config.textShadow);
        y += 10;
        if (config.showChunk) {
            HudStyle.drawLabeled(graphics, client.font, "CHK  ", this.chunk.toString(), x, y, config.textShadow);
            y += 10;
        }
        if (config.showSpeed) {
            HudStyle.drawLabeled(graphics, client.font, "SPD  ", this.speed.toString(), x, y, config.textShadow);
            y += 10;
        }
        if (config.showDimension) {
            HudStyle.drawLabeled(graphics, client.font, "DIM  ", this.dim.toString(), x, y, config.textShadow);
            y += 10;
        }
        if (config.showBiome) {
            HudStyle.drawLabeled(graphics, client.font, "BIO  ", this.biome.toString(), x, y, config.textShadow);
            y += 10;
        }
        if (config.showLight) {
            HudStyle.drawLabeled(graphics, client.font, "LIT  ", this.light.toString(), x, y, config.textShadow);
            y += 10;
        }
        if (config.showBlock) {
            HudStyle.drawLabeled(graphics, client.font, "BLK  ", this.block.toString(), x, y, config.textShadow);
            y += 10;
        }
        this.prepared = false;
        return y;
    }

    private void build(Minecraft client, InfoForgeConfig config) {
        LocalPlayer player = client.player;
        BlockPos pos = this.mutablePos.set(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        this.xyz.setLength(0);
        this.xyz.append(pos.getX());
        this.xyz.append(' ');
        this.xyz.append(pos.getY());
        this.xyz.append(' ');
        this.xyz.append(pos.getZ());
        this.xyz.append("  ");
        this.xyz.append(facingLabel(player.getDirection()));
        int yaw = Math.floorMod(Math.round(player.getYRot()), 360);
        this.xyz.append(' ');
        this.xyz.append(yaw);
        this.xyz.append('°');

        if (config.showChunk) {
            this.chunk.setLength(0);
            this.chunk.append(pos.getX() >> 4);
            this.chunk.append(' ');
            this.chunk.append(pos.getZ() >> 4);
            this.chunk.append("  (");
            this.chunk.append(Math.floorMod(pos.getX(), 16));
            this.chunk.append(' ');
            this.chunk.append(Math.floorMod(pos.getZ(), 16));
            this.chunk.append(')');
        }

        if (config.showSpeed) {
            Vec3 delta = player.getDeltaMovement();
            double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z) * 20.0;
            this.speed.setLength(0);
            appendOneDecimal(this.speed, horizontal);
            this.speed.append(" m/s");
        }

        if (config.showDimension) {
            this.dim.setLength(0);
            this.dim.append(client.level.dimension().identifier().getPath());
        }
        if (config.showBiome) {
            Holder<Biome> biomeHolder = client.level.getBiome(pos);
            this.biome.setLength(0);
            HudText.appendPath(this.biome, biomeHolder.getRegisteredName());
        }
        if (config.showLight) {
            int blockLight = client.level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = client.level.getBrightness(LightLayer.SKY, pos);
            int maxLight = client.level.getMaxLocalRawBrightness(pos);
            this.light.setLength(0);
            this.light.append(maxLight);
            this.light.append("  (b ");
            this.light.append(blockLight);
            this.light.append(" / s ");
            this.light.append(skyLight);
            this.light.append(')');
        }
        if (config.showBlock) {
            this.mutablePos.set(pos.getX(), pos.getY() - 1, pos.getZ());
            BlockState state = client.level.getBlockState(this.mutablePos);
            Block blockType = state.getBlock();
            Identifier id = BuiltInRegistries.BLOCK.getKey(blockType);
            this.block.setLength(0);
            this.block.append(id.getPath());
        }
    }

    private static void appendOneDecimal(StringBuilder out, double value) {
        int scaled = (int) Math.round(value * 10.0);
        out.append(scaled / 10);
        out.append('.');
        out.append(Math.abs(scaled % 10));
    }

    private static String facingLabel(Direction direction) {
        return switch (direction) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case WEST -> "W";
            case EAST -> "E";
            case UP -> "U";
            case DOWN -> "D";
        };
    }
}
