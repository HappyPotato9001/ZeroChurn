package dev.infoforge.client.hud;

import dev.infoforge.client.ui.InfoForgePalette;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

final class HudStyle {
    private HudStyle() {
    }

    static void drawBackdrop(GuiGraphics graphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.fill(x - 4, y - 3, x + width + 6, y + height + 2, InfoForgePalette.HUD_BACKDROP);
        graphics.fill(x - 4, y - 3, x - 2, y + height + 2, InfoForgePalette.HUD_ACCENT_BAR);
    }

    static void drawLabeled(
        GuiGraphics graphics,
        Font font,
        String label,
        String value,
        int x,
        int y,
        boolean shadow
    ) {
        drawLabeled(graphics, font, label, value, x, y, InfoForgePalette.HUD_VALUE, shadow);
    }

    static void drawLabeled(
        GuiGraphics graphics,
        Font font,
        String label,
        String value,
        int x,
        int y,
        int valueColor,
        boolean shadow
    ) {
        graphics.drawString(font, label, x, y, InfoForgePalette.HUD_LABEL, shadow);
        graphics.drawString(font, value, x + font.width(label), y, valueColor, shadow);
    }

    static int lineWidth(Font font, String label, String value) {
        return font.width(label) + font.width(value);
    }

    static int fpsColor(int fps) {
        if (fps >= 60) {
            return InfoForgePalette.OK;
        }
        if (fps >= 30) {
            return 0xFFE6C35C;
        }
        return InfoForgePalette.DANGER;
    }
}
