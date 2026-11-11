package dev.infoforge.client.config;

import dev.infoforge.client.ui.InfoForgeGhostButton;
import dev.infoforge.client.ui.InfoForgePalette;
import dev.infoforge.client.ui.InfoForgePrimaryButton;
import dev.infoforge.client.ui.InfoForgeUi;
import dev.infoforge.compat.ModCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * First viewport: brand InfoForge, one short subtitle, one primary action.
 */
public final class InfoForgeHomeScreen extends Screen {
    private final Screen parent;

    public InfoForgeHomeScreen(Screen parent) {
        super(Component.literal("InfoForge"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int primaryY = this.height / 2 + 28;
        this.addRenderableWidget(new InfoForgePrimaryButton(
            centerX - 110,
            primaryY,
            220,
            24,
            Component.literal("Configure HUD"),
            () -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new InfoForgeSettingsScreen(this));
                }
            }
        ));
        this.addRenderableWidget(new InfoForgeGhostButton(
            centerX - 60,
            primaryY + 36,
            120,
            18,
            Component.literal("Close"),
            this::onClose
        ));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        InfoForgeUi.drawAtmosphere(graphics, this.width, this.height);

        int centerX = this.width / 2;
        int brandY = this.height / 2 - 52;
        InfoForgeUi.drawBrandMark(graphics, this.font, centerX, brandY);
        InfoForgeUi.drawCentered(
            graphics,
            this.font,
            "Precision HUD for survival and exploration",
            centerX,
            brandY + 40,
            InfoForgePalette.TEXT_SECONDARY
        );

        String compat = ModCompat.companionStatusLine();
        InfoForgeUi.drawCentered(
            graphics,
            this.font,
            compat,
            centerX,
            this.height - 18,
            InfoForgePalette.TEXT_MUTED
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
