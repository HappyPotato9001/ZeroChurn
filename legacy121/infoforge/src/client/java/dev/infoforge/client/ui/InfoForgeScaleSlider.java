package dev.infoforge.client.ui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public final class InfoForgeScaleSlider extends AbstractSliderButton {
    private static final float MIN = 0.5f;
    private static final float MAX = 2.0f;

    private final DoubleSupplier getter;
    private final DoubleConsumer setter;

    public InfoForgeScaleSlider(int x, int y, int width, int height, DoubleSupplier getter, DoubleConsumer setter) {
        super(x, y, width, height, Component.literal(""), toSlider(getter.getAsDouble()));
        this.getter = getter;
        this.setter = setter;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        float scale = fromSlider(this.value);
        this.setMessage(Component.literal(String.format("HUD Scale  %.1fx", scale)));
    }

    @Override
    protected void applyValue() {
        this.setter.accept(fromSlider(this.value));
    }

    public void syncFromConfig() {
        this.setValue(toSlider(this.getter.getAsDouble()));
        this.updateMessage();
    }

    private static double toSlider(double scale) {
        double clamped = Math.max(MIN, Math.min(MAX, scale));
        return (clamped - MIN) / (MAX - MIN);
    }

    private static float fromSlider(double value) {
        return (float) (MIN + value * (MAX - MIN));
    }
}
