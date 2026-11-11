package dev.infoforge.client.compat;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Bridges small Minecraft client API differences across 26.1 / 26.2 / 26.3
 * so one InfoForge source tree can build for each target.
 */
public final class ClientAccess {
    private static final Method SET_SCREEN_ON_MINECRAFT;
    private static final Method SET_SCREEN_ON_GUI;
    private static final Method SCREEN_ON_GUI;
    private static final Field SCREEN_ON_MINECRAFT;
    private static final Field HIDE_GUI;
    private static final Field GUI_HUD;
    private static final Method HUD_IS_HIDDEN;
    private static final InputConstants.Type KEYBOARD_TYPE = resolveKeyboardType();

    static {
        Method setOnMc = null;
        Method setOnGui = null;
        Method screenOnGui = null;
        Field screenOnMc = null;
        Field hideGui = null;
        Field guiHud = null;
        Method hudIsHidden = null;

        try {
            setOnMc = Minecraft.class.getMethod("setScreen", Screen.class);
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            Class<?> gui = Class.forName("net.minecraft.client.gui.Gui");
            try {
                setOnGui = gui.getMethod("setScreen", Screen.class);
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                screenOnGui = gui.getMethod("screen");
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                guiHud = gui.getField("hud");
                hudIsHidden = guiHud.getType().getMethod("isHidden");
            } catch (ReflectiveOperationException ignored) {
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            screenOnMc = Minecraft.class.getField("screen");
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            hideGui = Class.forName("net.minecraft.client.Options").getField("hideGui");
        } catch (ReflectiveOperationException ignored) {
        }

        SET_SCREEN_ON_MINECRAFT = setOnMc;
        SET_SCREEN_ON_GUI = setOnGui;
        SCREEN_ON_GUI = screenOnGui;
        SCREEN_ON_MINECRAFT = screenOnMc;
        HIDE_GUI = hideGui;
        GUI_HUD = guiHud;
        HUD_IS_HIDDEN = hudIsHidden;
    }

    private ClientAccess() {
    }

    public static InputConstants.Type keyboardType() {
        return KEYBOARD_TYPE;
    }

    public static void setScreen(Minecraft client, Screen screen) {
        try {
            if (SET_SCREEN_ON_GUI != null) {
                SET_SCREEN_ON_GUI.invoke(client.gui, screen);
                return;
            }
            if (SET_SCREEN_ON_MINECRAFT != null) {
                SET_SCREEN_ON_MINECRAFT.invoke(client, screen);
                return;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        client.setScreenAndShow(screen);
    }

    public static Screen currentScreen(Minecraft client) {
        try {
            if (SCREEN_ON_GUI != null) {
                Object value = SCREEN_ON_GUI.invoke(client.gui);
                return value instanceof Screen screen ? screen : null;
            }
            if (SCREEN_ON_MINECRAFT != null) {
                Object value = SCREEN_ON_MINECRAFT.get(client);
                return value instanceof Screen screen ? screen : null;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }

    public static boolean isHudHidden(Minecraft client) {
        try {
            if (GUI_HUD != null && HUD_IS_HIDDEN != null) {
                Object hud = GUI_HUD.get(client.gui);
                Object hidden = HUD_IS_HIDDEN.invoke(hud);
                return hidden instanceof Boolean b && b;
            }
            if (HIDE_GUI != null) {
                Object hidden = HIDE_GUI.get(client.options);
                return hidden instanceof Boolean b && b;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return false;
    }

    private static InputConstants.Type resolveKeyboardType() {
        try {
            return InputConstants.Type.valueOf("KEYBOARD");
        } catch (IllegalArgumentException ignored) {
            return InputConstants.Type.valueOf("KEYSYM");
        }
    }
}
