package dev.infoforge.client.hud;

import dev.infoforge.client.ui.InfoForgePalette;
import dev.infoforge.config.InfoForgeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

final class FpsHudPanel {
    private final FrameTimeTracker frameTimes = new FrameTimeTracker();
    private final StringBuilder value = new StringBuilder(64);
    private int lastFps;
    private boolean prepared;

    void prepare(Minecraft client, InfoForgeConfig config) {
        if (!config.showFps) {
            this.prepared = false;
            return;
        }
        buildValue(client, config, true);
        this.prepared = true;
    }

    int measure(Minecraft client, InfoForgeConfig config, Font font) {
        if (!config.showFps) {
            return 0;
        }
        if (!this.prepared) {
            buildValue(client, config, false);
        }
        return HudStyle.lineWidth(font, "FPS  ", this.value.toString());
    }

    int render(GuiGraphicsExtractor graphics, Minecraft client, InfoForgeConfig config, int x, int y) {
        if (!config.showFps) {
            return y;
        }
        if (!this.prepared) {
            buildValue(client, config, true);
        }
        int color = config.colorizeFps ? HudStyle.fpsColor(this.lastFps) : InfoForgePalette.HUD_VALUE;
        HudStyle.drawLabeled(graphics, client.font, "FPS  ", this.value.toString(), x, y, color, config.textShadow);
        this.prepared = false;
        return y + 10;
    }

    private void buildValue(Minecraft client, InfoForgeConfig config, boolean recordSample) {
        long frameNs = client.getFrameTimeNs();
        if (recordSample) {
            this.frameTimes.record(frameNs);
        }

        this.lastFps = client.getFps();
        this.value.setLength(0);
        this.value.append(this.lastFps);

        int onePercent = this.frameTimes.onePercentLowFps();
        if (onePercent > 0) {
            this.value.append("  ·  1% ");
            this.value.append(onePercent);
        }

        if (frameNs > 0L) {
            this.value.append("  ·  ");
            this.value.append(frameNs / 1_000_000L);
            this.value.append('.');
            this.value.append((frameNs / 100_000L) % 10L);
            this.value.append(" ms");
        }

        if (config.showMemory) {
            Runtime runtime = Runtime.getRuntime();
            long usedMb = (runtime.totalMemory() - runtime.freeMemory()) >> 20;
            long maxMb = runtime.maxMemory() >> 20;
            this.value.append("  ·  ");
            this.value.append(usedMb);
            this.value.append('/');
            this.value.append(maxMb);
            this.value.append(" MB");
        }
    }
}
