package dev.infoforge.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class InfoForgeGhostButton extends AbstractWidget {
    private final Runnable onPress;

    public InfoForgeGhostButton(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.onPress.run();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        boolean hot = this.isHoveredOrFocused();
        if (hot) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x33182028);
            graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, InfoForgePalette.ACCENT_DIM);
        }
        String label = this.getMessage().getString();
        int textX = this.getX() + (this.width - Minecraft.getInstance().font.width(label)) / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        int color = hot ? InfoForgePalette.TEXT_PRIMARY : InfoForgePalette.TEXT_SECONDARY;
        graphics.text(Minecraft.getInstance().font, label, textX, textY, color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
