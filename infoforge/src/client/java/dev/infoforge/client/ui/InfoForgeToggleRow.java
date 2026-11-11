package dev.infoforge.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Settings row: label + rectangular ON/OFF indicator. Not a rounded pill.
 */
public final class InfoForgeToggleRow extends AbstractWidget {
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private final String detail;

    public InfoForgeToggleRow(
        int x,
        int y,
        int width,
        Component label,
        String detail,
        BooleanSupplier getter,
        Consumer<Boolean> setter
    ) {
        super(x, y, width, 22, label);
        this.detail = detail;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        boolean next = !this.getter.getAsBoolean();
        this.setter.accept(next);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        boolean on = this.getter.getAsBoolean();
        boolean hot = this.isHoveredOrFocused();
        int bg = hot ? InfoForgePalette.BG_ROW_HOVER : InfoForgePalette.BG_ROW;
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
        if (hot) {
            graphics.fill(this.getX(), this.getY(), this.getX() + 2, this.getY() + this.height, InfoForgePalette.ACCENT);
        }

        Font font = Minecraft.getInstance().font;
        graphics.text(font, this.getMessage().getString(), this.getX() + 10, this.getY() + 7, InfoForgePalette.TEXT_PRIMARY, false);
        if (this.detail != null && !this.detail.isEmpty()) {
            int detailX = this.getX() + 10 + font.width(this.getMessage().getString()) + 8;
            graphics.text(font, this.detail, detailX, this.getY() + 7, InfoForgePalette.TEXT_MUTED, false);
        }

        String state = on ? "ON" : "OFF";
        int boxW = 34;
        int boxH = 14;
        int boxX = this.getX() + this.width - boxW - 8;
        int boxY = this.getY() + (this.height - boxH) / 2;
        int boxFill = on ? InfoForgePalette.ACCENT_DIM : 0xFF243040;
        int boxText = on ? InfoForgePalette.TEXT_ON_ACCENT : InfoForgePalette.TEXT_SECONDARY;
        graphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, boxFill);
        graphics.fill(boxX, boxY, boxX + boxW, boxY + 1, on ? InfoForgePalette.ACCENT : InfoForgePalette.BG_PANEL_EDGE);
        graphics.fill(boxX, boxY + boxH - 1, boxX + boxW, boxY + boxH, on ? InfoForgePalette.ACCENT : InfoForgePalette.BG_PANEL_EDGE);
        graphics.text(font, state, boxX + (boxW - font.width(state)) / 2, boxY + 3, boxText, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
        output.add(NarratedElementType.HINT, this.getter.getAsBoolean() ? "on" : "off");
    }
}
