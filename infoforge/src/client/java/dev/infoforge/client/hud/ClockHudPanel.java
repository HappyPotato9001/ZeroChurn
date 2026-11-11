package dev.infoforge.client.hud;

import dev.infoforge.config.InfoForgeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

final class ClockHudPanel {
    private final StringBuilder value = new StringBuilder(48);
    private boolean prepared;

    void prepare(Minecraft client, InfoForgeConfig config) {
        if (!config.showClock || client.level == null) {
            this.prepared = false;
            return;
        }
        build(client);
        this.prepared = true;
    }

    int measure(Minecraft client, InfoForgeConfig config, Font font) {
        if (!config.showClock || client.level == null) {
            return 0;
        }
        if (!this.prepared) {
            build(client);
        }
        return HudStyle.lineWidth(font, "DAY  ", this.value.toString());
    }

    int render(GuiGraphicsExtractor graphics, Minecraft client, InfoForgeConfig config, int x, int y) {
        if (!config.showClock || client.level == null) {
            return y;
        }
        if (!this.prepared) {
            build(client);
        }
        HudStyle.drawLabeled(graphics, client.font, "DAY  ", this.value.toString(), x, y, config.textShadow);
        this.prepared = false;
        return y + 10;
    }

    private void build(Minecraft client) {
        long dayTime = client.level.getOverworldClockTime();
        long timeOfDay = dayTime % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }
        long day = dayTime / 24000L;
        int hour = (int) ((timeOfDay / 1000L + 6L) % 24L);
        int minute = (int) ((timeOfDay % 1000L) * 60L / 1000L);

        this.value.setLength(0);
        this.value.append(day);
        this.value.append("  ·  ");
        HudText.appendTwoDigits(this.value, hour);
        this.value.append(':');
        HudText.appendTwoDigits(this.value, minute);
        if (client.level.isRaining()) {
            this.value.append(client.level.isThundering() ? "  ·  thunder" : "  ·  rain");
        } else {
            // Clear / night cue without allocating weather strings beyond constants.
            if (timeOfDay >= 13000L && timeOfDay < 23000L) {
                this.value.append("  ·  night");
            } else {
                this.value.append("  ·  day");
            }
        }
    }
}
