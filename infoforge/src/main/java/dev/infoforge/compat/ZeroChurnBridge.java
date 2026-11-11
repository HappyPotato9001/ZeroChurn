package dev.infoforge.compat;

import java.lang.reflect.Method;

/**
 * Optional read-only bridge to ZeroChurn stats. Prefers {@code hudLine()} so
 * InfoForge never drives ZeroChurn's rate sampler every frame.
 */
public final class ZeroChurnBridge {
    private static final Method HUD_LINE;
    private static final Method SUMMARY;

    static {
        Method hudLine = null;
        Method summary = null;
        if (ModCompat.isZeroChurnLoaded()) {
            try {
                Class<?> type = Class.forName("dev.zerochurn.metrics.ZeroChurnStats");
                try {
                    hudLine = type.getMethod("hudLine");
                } catch (NoSuchMethodException ignored) {
                }
                summary = type.getMethod("summary");
            } catch (ReflectiveOperationException ignored) {
                hudLine = null;
                summary = null;
            }
        }
        HUD_LINE = hudLine;
        SUMMARY = summary;
    }

    private ZeroChurnBridge() {
    }

    public static String summaryOrNull() {
        Method method = HUD_LINE != null ? HUD_LINE : SUMMARY;
        if (method == null) {
            return null;
        }
        try {
            Object value = method.invoke(null);
            return value instanceof String text ? text : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
