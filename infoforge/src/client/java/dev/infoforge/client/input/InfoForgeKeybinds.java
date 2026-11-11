package dev.infoforge.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.infoforge.InfoForge;
import dev.infoforge.client.compat.ClientAccess;
import dev.infoforge.client.config.InfoForgeHomeScreen;
import dev.infoforge.config.InfoForgeConfig;
import dev.infoforge.config.InfoForgeConfigLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class InfoForgeKeybinds {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(InfoForge.MOD_ID, "infoforge")
    );

    private static KeyMapping toggleHud;
    private static KeyMapping toggleCoords;
    private static KeyMapping toggleFps;
    private static KeyMapping toggleClock;
    private static KeyMapping cycleAnchor;
    private static KeyMapping openConfig;

    private InfoForgeKeybinds() {
    }

    public static void register() {
        InputConstants.Type keyboard = ClientAccess.keyboardType();

        toggleHud = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.toggle_hud",
            keyboard,
            InputConstants.KEY_F8,
            CATEGORY
        ));
        toggleCoords = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.toggle_coords",
            keyboard,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        toggleFps = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.toggle_fps",
            keyboard,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        toggleClock = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.toggle_clock",
            keyboard,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        cycleAnchor = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.cycle_anchor",
            keyboard,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.infoforge.open_config",
            keyboard,
            InputConstants.KEY_RSHIFT,
            CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            InfoForgeConfig config = InfoForgeConfigLoader.get();
            boolean dirty = false;

            while (toggleHud.consumeClick()) {
                config.hudEnabled = !config.hudEnabled;
                dirty = true;
            }
            while (toggleCoords.consumeClick()) {
                config.showCoords = !config.showCoords;
                dirty = true;
            }
            while (toggleFps.consumeClick()) {
                config.showFps = !config.showFps;
                dirty = true;
            }
            while (toggleClock.consumeClick()) {
                config.showClock = !config.showClock;
                dirty = true;
            }
            while (cycleAnchor.consumeClick()) {
                config.applyAnchorPreset((config.anchor + 1) % 4);
                dirty = true;
            }
            while (openConfig.consumeClick()) {
                ClientAccess.setScreen(client, new InfoForgeHomeScreen(ClientAccess.currentScreen(client)));
            }

            if (dirty) {
                InfoForgeConfigLoader.save();
            }
        });
    }
}
