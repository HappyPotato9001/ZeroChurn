package dev.infoforge.client.config;

import dev.infoforge.client.ui.InfoForgeGhostButton;
import dev.infoforge.client.ui.InfoForgePalette;
import dev.infoforge.client.ui.InfoForgePrimaryButton;
import dev.infoforge.client.ui.InfoForgeScaleSlider;
import dev.infoforge.client.ui.InfoForgeToggleRow;
import dev.infoforge.client.ui.InfoForgeUi;
import dev.infoforge.compat.ModCompat;
import dev.infoforge.config.InfoForgeConfig;
import dev.infoforge.config.InfoForgeConfigLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Professional settings layout: sectioned toggle rows, anchors, scale.
 */
public final class InfoForgeSettingsScreen extends Screen {
    private final Screen parent;

    public InfoForgeSettingsScreen(Screen parent) {
        super(Component.literal("InfoForge Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        InfoForgeConfig config = InfoForgeConfigLoader.get();
        int panelW = Math.min(600, this.width - 40);
        int left = (this.width - panelW) / 2;
        int colW = (panelW - 16) / 2;
        int y = 64;

        this.addRenderableWidget(row(left, y, colW, "HUD Enabled", "master", () -> config.hudEnabled, v -> config.hudEnabled = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Coordinates", "xyz / yaw", () -> config.showCoords, v -> config.showCoords = v));
        y += 24;
        this.addRenderableWidget(row(left, y, colW, "FPS", "1% low + frametime", () -> config.showFps, v -> config.showFps = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Clock", "day / weather", () -> config.showClock, v -> config.showClock = v));
        y += 24;
        this.addRenderableWidget(row(left, y, colW, "Chunk", "chunk + local", () -> config.showChunk, v -> config.showChunk = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Speed", "horizontal m/s", () -> config.showSpeed, v -> config.showSpeed = v));
        y += 24;
        this.addRenderableWidget(row(left, y, colW, "Biome", null, () -> config.showBiome, v -> config.showBiome = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Dimension", null, () -> config.showDimension, v -> config.showDimension = v));
        y += 24;
        this.addRenderableWidget(row(left, y, colW, "Light Level", "block / sky", () -> config.showLight, v -> config.showLight = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Block Under Feet", null, () -> config.showBlock, v -> config.showBlock = v));
        y += 24;
        this.addRenderableWidget(row(left, y, colW, "Memory", "read-only", () -> config.showMemory, v -> config.showMemory = v));
        this.addRenderableWidget(row(left + colW + 16, y, colW, "Colorize FPS", "green / amber / red", () -> config.colorizeFps, v -> config.colorizeFps = v));
        y += 24;
        this.addRenderableWidget(row(
            left,
            y,
            panelW,
            "ZeroChurn Line",
            ModCompat.isZeroChurnLoaded() ? "linked companion stats" : "optional — install ZeroChurn",
            () -> config.showZeroChurnLine,
            v -> config.showZeroChurnLine = v
        ));
        y += 30;

        int btnW = (panelW - 18) / 4;
        this.addRenderableWidget(anchorButton(left, y, btnW, "Top Left", 0, config));
        this.addRenderableWidget(anchorButton(left + btnW + 6, y, btnW, "Top Right", 1, config));
        this.addRenderableWidget(anchorButton(left + (btnW + 6) * 2, y, btnW, "Bottom Left", 2, config));
        this.addRenderableWidget(anchorButton(left + (btnW + 6) * 3, y, btnW, "Bottom Right", 3, config));
        y += 28;

        this.addRenderableWidget(new InfoForgeScaleSlider(
            left,
            y,
            panelW,
            20,
            () -> config.hudScale,
            value -> config.hudScale = (float) value
        ));

        int footerY = this.height - 34;
        this.addRenderableWidget(new InfoForgePrimaryButton(
            this.width / 2 - 110,
            footerY,
            140,
            22,
            Component.literal("Save & Close"),
            this::onClose
        ));
        this.addRenderableWidget(new InfoForgeGhostButton(
            this.width / 2 + 40,
            footerY,
            70,
            22,
            Component.literal("Back"),
            () -> {
                InfoForgeConfigLoader.save();
                if (this.minecraft != null) {
                    this.minecraft.setScreen(this.parent);
                }
            }
        ));
    }

    private InfoForgeGhostButton anchorButton(int x, int y, int w, String label, int preset, InfoForgeConfig config) {
        return new InfoForgeGhostButton(x, y, w, 20, Component.literal(label), () -> {
            config.applyAnchorPreset(preset);
            InfoForgeConfigLoader.save();
        });
    }

    private static InfoForgeToggleRow row(
        int x,
        int y,
        int w,
        String label,
        String detail,
        java.util.function.BooleanSupplier getter,
        java.util.function.Consumer<Boolean> setter
    ) {
        return new InfoForgeToggleRow(x, y, w, Component.literal(label), detail, getter, setter);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        InfoForgeUi.drawAtmosphere(graphics, this.width, this.height);

        int panelW = Math.min(600, this.width - 40);
        int left = (this.width - panelW) / 2;
        InfoForgeUi.drawPanel(graphics, left - 12, 24, panelW + 24, this.height - 68);

        graphics.drawString(this.font, "InfoForge", left, 32, InfoForgePalette.TEXT_PRIMARY, false);
        graphics.drawString(this.font, "HUD Panels", left + this.font.width("InfoForge") + 10, 32, InfoForgePalette.TEXT_MUTED, false);
        InfoForgeUi.drawHairline(graphics, left, 44, panelW);

        String status = ModCompat.isZeroChurnLoaded()
            ? "Companion link: ZeroChurn detected"
            : "Runs standalone — install ZeroChurn for optional GC stats";
        graphics.drawString(this.font, status, left, this.height - 50, InfoForgePalette.TEXT_MUTED, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        InfoForgeConfigLoader.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
