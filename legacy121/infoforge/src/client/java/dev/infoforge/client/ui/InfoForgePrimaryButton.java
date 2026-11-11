package dev.infoforge.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class InfoForgePrimaryButton extends AbstractWidget {
    private final Runnable onPress;

    public InfoForgePrimaryButton(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.onPress.run();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hot = this.isHoveredOrFocused();
        int fill = hot ? InfoForgePalette.ACCENT : InfoForgePalette.ACCENT_DIM;
        int edge = hot ? InfoForgePalette.TEXT_PRIMARY : InfoForgePalette.ACCENT;
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, fill);
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, edge);
        graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, edge);
        graphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, edge);
        graphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, edge);
        String label = this.getMessage().getString();
        int textX = this.getX() + (this.width - Minecraft.getInstance().font.width(label)) / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        graphics.drawString(Minecraft.getInstance().font, label, textX, textY, InfoForgePalette.TEXT_ON_ACCENT, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
