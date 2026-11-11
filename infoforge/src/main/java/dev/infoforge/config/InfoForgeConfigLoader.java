package dev.infoforge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.infoforge.InfoForge;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InfoForgeConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final InfoForgeConfig CURRENT = new InfoForgeConfig();
    private static boolean loaded;

    private InfoForgeConfigLoader() {
    }

    public static InfoForgeConfig get() {
        if (!loaded) {
            load();
        }
        return CURRENT;
    }

    public static synchronized InfoForgeConfig load() {
        Path path = configPath();
        try {
            if (!Files.exists(path)) {
                CURRENT.copyFrom(new InfoForgeConfig());
                CURRENT.sanitize();
                write(path, CURRENT);
                loaded = true;
                return CURRENT;
            }
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                InfoForgeConfig parsed = GSON.fromJson(reader, InfoForgeConfig.class);
                if (parsed == null) {
                    parsed = new InfoForgeConfig();
                }
                parsed.sanitize();
                CURRENT.copyFrom(parsed);
            }
        } catch (Exception e) {
            InfoForge.LOGGER.warn("Failed to load InfoForge config; using defaults", e);
            CURRENT.copyFrom(new InfoForgeConfig());
            CURRENT.sanitize();
        }
        loaded = true;
        return CURRENT;
    }

    public static synchronized void save() {
        CURRENT.sanitize();
        try {
            write(configPath(), CURRENT);
        } catch (Exception e) {
            InfoForge.LOGGER.warn("Failed to save InfoForge config", e);
        }
    }

    private static void write(Path path, InfoForgeConfig config) throws Exception {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("infoforge.json");
    }
}
