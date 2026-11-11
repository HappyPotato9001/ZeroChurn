package dev.infoforge.config;

public final class InfoForgeConfig {
    public boolean hudEnabled = true;
    public boolean showCoords = true;
    public boolean showFps = true;
    public boolean showClock = true;
    public boolean showBiome = true;
    public boolean showDimension = true;
    public boolean showLight = true;
    public boolean showBlock = true;
    public boolean showChunk = true;
    public boolean showSpeed = false;
    public boolean showMemory = false;
    public boolean showZeroChurnLine = true;
    public boolean colorizeFps = true;

    public int hudX = 4;
    public int hudY = 4;
    public float hudScale = 1.0f;
    public int textColor = 0xE0FFFFFF;
    public boolean textShadow = true;
    /** 0=top-left, 1=top-right, 2=bottom-left, 3=bottom-right */
    public int anchor = 0;

    public void copyFrom(InfoForgeConfig other) {
        this.hudEnabled = other.hudEnabled;
        this.showCoords = other.showCoords;
        this.showFps = other.showFps;
        this.showClock = other.showClock;
        this.showBiome = other.showBiome;
        this.showDimension = other.showDimension;
        this.showLight = other.showLight;
        this.showBlock = other.showBlock;
        this.showChunk = other.showChunk;
        this.showSpeed = other.showSpeed;
        this.showMemory = other.showMemory;
        this.showZeroChurnLine = other.showZeroChurnLine;
        this.colorizeFps = other.colorizeFps;
        this.hudX = other.hudX;
        this.hudY = other.hudY;
        this.hudScale = other.hudScale;
        this.textColor = other.textColor;
        this.textShadow = other.textShadow;
        this.anchor = other.anchor;
    }

    public void sanitize() {
        if (this.hudScale < 0.5f) {
            this.hudScale = 0.5f;
        }
        if (this.hudScale > 3.0f) {
            this.hudScale = 3.0f;
        }
        if (this.hudX < 0) {
            this.hudX = 0;
        }
        if (this.hudY < 0) {
            this.hudY = 0;
        }
        if (this.anchor < 0 || this.anchor > 3) {
            this.anchor = 0;
        }
    }

    public void applyAnchorPreset(int preset) {
        this.anchor = preset;
        switch (preset) {
            case 1 -> {
                this.hudX = 4;
                this.hudY = 4;
                // right side resolved at render using screen width
            }
            case 2 -> {
                this.hudX = 4;
                this.hudY = 4;
            }
            case 3 -> {
                this.hudX = 4;
                this.hudY = 4;
            }
            default -> {
                this.hudX = 4;
                this.hudY = 4;
                this.anchor = 0;
            }
        }
    }
}
