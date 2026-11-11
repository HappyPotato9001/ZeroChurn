package dev.infoforge.client.hud;

import dev.infoforge.client.compat.ClientAccess;
import dev.infoforge.compat.ZeroChurnBridge;
import dev.infoforge.config.InfoForgeConfig;
import dev.infoforge.config.InfoForgeConfigLoader;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

/**
 * Overlay orchestrator with anchored, compact HUD strip.
 * Scratch builders/positions live on this render-thread instance only.
 */
public final class InfoHudElement implements HudElement {
    private final FpsHudPanel fpsPanel = new FpsHudPanel();
    private final CoordsHudPanel coordsPanel = new CoordsHudPanel();
    private final ClockHudPanel clockPanel = new ClockHudPanel();

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        InfoForgeConfig config = InfoForgeConfigLoader.get();
        if (!config.hudEnabled) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || ClientAccess.isHudHidden(client)) {
            return;
        }

        this.fpsPanel.prepare(client, config);
        this.coordsPanel.prepare(client, config);
        this.clockPanel.prepare(client, config);

        int width = 0;
        width = Math.max(width, this.fpsPanel.measure(client, config, client.font));
        width = Math.max(width, this.coordsPanel.measure(client, config, client.font));
        width = Math.max(width, this.clockPanel.measure(client, config, client.font));

        String zero = null;
        if (config.showZeroChurnLine) {
            zero = ZeroChurnBridge.summaryOrNull();
            if (zero != null) {
                width = Math.max(width, client.font.width(zero));
            }
        }

        int lines = 0;
        if (config.showFps) {
            lines++;
        }
        lines += this.coordsPanel.lineCount(config);
        if (config.showClock) {
            lines++;
        }
        if (zero != null) {
            lines++;
        }
        if (lines == 0 || width <= 0) {
            return;
        }

        int height = lines * 10;
        int guiWidth = client.getWindow().getGuiScaledWidth();
        int guiHeight = client.getWindow().getGuiScaledHeight();
        float scale = config.hudScale <= 0.0f ? 1.0f : config.hudScale;

        int x = config.hudX;
        int y = config.hudY;
        int scaledW = (int) (width * scale) + 10;
        int scaledH = (int) (height * scale) + 8;
        switch (config.anchor) {
            case 1 -> { // top-right
                x = Math.max(4, (int) ((guiWidth - scaledW) / scale) - config.hudX);
                y = config.hudY;
            }
            case 2 -> { // bottom-left
                x = config.hudX;
                y = Math.max(4, (int) ((guiHeight - scaledH) / scale) - config.hudY);
            }
            case 3 -> { // bottom-right
                x = Math.max(4, (int) ((guiWidth - scaledW) / scale) - config.hudX);
                y = Math.max(4, (int) ((guiHeight - scaledH) / scale) - config.hudY);
            }
            default -> {
                x = config.hudX;
                y = config.hudY;
            }
        }

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        if (scale != 1.0f) {
            pose.scale(scale, scale);
        }

        HudStyle.drawBackdrop(graphics, x, y, width, height);

        int cursorY = y;
        cursorY = this.fpsPanel.render(graphics, client, config, x, cursorY);
        cursorY = this.coordsPanel.render(graphics, client, config, x, cursorY);
        cursorY = this.clockPanel.render(graphics, client, config, x, cursorY);
        if (zero != null) {
            graphics.text(client.font, zero, x, cursorY, config.textColor, config.textShadow);
        }

        pose.popMatrix();
    }
}
