package dev.infoforge.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.infoforge.InfoForge;
import dev.infoforge.client.config.InfoForgeHomeScreen;
import dev.infoforge.config.InfoForgeConfig;
import dev.infoforge.config.InfoForgeConfigLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.toggle_hud",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            CATEGORY
        ));
        toggleCoords = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.toggle_coords",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        toggleFps = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.toggle_fps",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        toggleClock = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.toggle_clock",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        cycleAnchor = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.cycle_anchor",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
        ));
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.infoforge.open_config",
            InputConstants.Type.KEYSYM,
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
                client.setScreen(new InfoForgeHomeScreen(client.screen));
            }

            if (dirty) {
                InfoForgeConfigLoader.save();
            }
        });
    }
}
