package dev.zerochurn.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zerochurn.ZeroChurn;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ZeroChurnConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ZeroChurnConfig CURRENT = new ZeroChurnConfig();
    private static boolean loaded;

    private ZeroChurnConfigLoader() {
    }

    public static ZeroChurnConfig get() {
        if (!loaded) {
            load();
        }
        return CURRENT;
    }

    public static synchronized ZeroChurnConfig load() {
        Path path = configPath();
        try {
            if (!Files.exists(path)) {
                CURRENT.copyFrom(new ZeroChurnConfig());
                CURRENT.sanitize();
                write(path, CURRENT);
                loaded = true;
                return CURRENT;
            }
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                ZeroChurnConfig parsed = GSON.fromJson(reader, ZeroChurnConfig.class);
                if (parsed == null) {
                    parsed = new ZeroChurnConfig();
                }
                parsed.sanitize();
                CURRENT.copyFrom(parsed);
            }
        } catch (Exception e) {
            ZeroChurn.LOGGER.warn("Failed to load ZeroChurn config; using defaults", e);
            CURRENT.copyFrom(new ZeroChurnConfig());
            CURRENT.sanitize();
        }
        loaded = true;
        return CURRENT;
    }

    public static synchronized void save() {
        CURRENT.sanitize();
        try {
            write(configPath(), CURRENT);
        } catch (IOException e) {
            ZeroChurn.LOGGER.warn("Failed to save ZeroChurn config", e);
        }
    }

    private static void write(Path path, ZeroChurnConfig config) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("zerochurn.json");
    }
}
