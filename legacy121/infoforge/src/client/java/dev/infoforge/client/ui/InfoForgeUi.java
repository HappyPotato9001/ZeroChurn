package dev.infoforge.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public final class InfoForgeUi {
    private InfoForgeUi() {
    }

    public static void drawAtmosphere(GuiGraphics graphics, int width, int height) {
        graphics.fillGradient(0, 0, width, height, 0xFF070B10, 0xFF121A24);
        graphics.fillGradient(0, 0, width, height / 2, 0x55202A36, 0x00000000);
        graphics.fill(0, height - 48, width, height, 0x66060A10);
        // Soft teal wash in the upper-right — atmospheric, not a glow bloom.
        graphics.fillGradient(width / 2, 0, width, height / 3, 0x00000000, 0x183DB8A8);
    }

    public static void drawPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x, y, x + w, y + h, InfoForgePalette.BG_PANEL);
        graphics.fill(x, y, x + w, y + 1, InfoForgePalette.BG_PANEL_EDGE);
        graphics.fill(x, y + h - 1, x + w, y + h, InfoForgePalette.BG_PANEL_EDGE);
        graphics.fill(x, y, x + 1, y + h, InfoForgePalette.BG_PANEL_EDGE);
        graphics.fill(x + w - 1, y, x + w, y + h, InfoForgePalette.BG_PANEL_EDGE);
        graphics.fill(x, y, x + 2, y + h, InfoForgePalette.ACCENT_DIM);
    }

    public static void drawHairline(GuiGraphics graphics, int x, int y, int w) {
        graphics.fill(x, y, x + w, y + 1, 0x55344A5C);
    }

    public static void drawBrandMark(GuiGraphics graphics, Font font, int centerX, int y) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(centerX, y);
        pose.scale(2.6f, 2.6f);
        int width = font.width("InfoForge");
        graphics.drawString(font, "InfoForge", -width / 2, 0, InfoForgePalette.TEXT_PRIMARY, false);
        pose.popMatrix();

        int barW = 72;
        graphics.fill(centerX - barW / 2, y + 28, centerX + barW / 2, y + 30, InfoForgePalette.ACCENT);
    }

    public static void drawCentered(GuiGraphics graphics, Font font, String text, int centerX, int y, int color) {
        graphics.drawString(font, text, centerX - font.width(text) / 2, y, color, false);
    }

    public static void drawSectionLabel(GuiGraphics graphics, Font font, String label, int x, int y) {
        graphics.drawString(font, label, x, y, InfoForgePalette.TEXT_MUTED, false);
        graphics.fill(x, y + 11, x + font.width(label), y + 12, 0x403DB8A8);
    }
}
