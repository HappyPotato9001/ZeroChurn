package dev.infoforge.client.hud;

final class HudText {
    private HudText() {
    }

    static void appendPath(StringBuilder out, String registeredName) {
        if (registeredName == null || registeredName.isEmpty()) {
            out.append('?');
            return;
        }
        int colon = registeredName.indexOf(':');
        if (colon >= 0 && colon + 1 < registeredName.length()) {
            out.append(registeredName, colon + 1, registeredName.length());
        } else {
            out.append(registeredName);
        }
    }

    static void appendTwoDigits(StringBuilder out, int value) {
        if (value < 10) {
            out.append('0');
        }
        out.append(value);
    }
}
