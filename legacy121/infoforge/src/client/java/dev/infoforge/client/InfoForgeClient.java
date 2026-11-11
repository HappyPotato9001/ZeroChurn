package dev.infoforge.client;

import dev.infoforge.InfoForge;
import dev.infoforge.compat.CompatibilityReport;
import dev.infoforge.config.InfoForgeConfigLoader;
import dev.infoforge.client.hud.InfoHudElement;
import dev.infoforge.client.input.InfoForgeKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public final class InfoForgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InfoForgeConfigLoader.load();
        InfoForgeKeybinds.register();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(InfoForge.MOD_ID, "overlay"), new InfoHudElement());
        CompatibilityReport.log();
        InfoForge.LOGGER.info("InfoForge client HUD ready (1.21.11)");
    }
}
