package de.ep.astralcores.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import de.ep.astralcores.AstralCores;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    // Target configuration file path within the system directories
    private static final File configFile = FabricLoader.getInstance().getConfigDir()
            .resolve("astral_cores").resolve("config.json5").toFile();

    // Configuration file engine parser library builder instance
    private static final Jankson jankson = Jankson.builder().build();

    // Holds the active configuration values loaded in memory
    private static Config config;

    // Reads the file from disk or generates defaults if missing
    public static void load() {
        if (!configFile.exists()) {
            config = new Config();
            save();
            return;
        }
        try {
            JsonObject jsonObject = jankson.load(configFile);
            config = jankson.fromJson(jsonObject, Config.class);

            // Enforce the minimum structure spawn radius
            if (config.general.structure_spawn_radius < 1500) {
                int oldRadius = config.general.structure_spawn_radius;

                config.general.structure_spawn_radius = 1500;

                AstralCores.LOGGER.warn(
                        "Invalid structure_spawn_radius value {} detected. " +
                                "The minimum allowed value is 1500. " +
                                "Resetting structure_spawn_radius to 1500.",
                        oldRadius
                );
            }

        } catch (IOException | SyntaxError e) {
            AstralCores.LOGGER.error("Failed to parse or read astralcores config.json5 file! Reverting to defaults.", e);
            config = new Config();
        }
    }

    // Serializes current configuration values and writes them to disk
    public static void save() {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            if (config == null) {
                config = new Config();
            }
            JsonElement jsonElement = jankson.toJson(config);
            String json5String = jsonElement.toJson(true, true);
            try (FileOutputStream out = new FileOutputStream(configFile)) {
                out.write(json5String.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            AstralCores.LOGGER.error("Critical error encountered while trying to write astralcores config.json5 to disk!", e);
        }
    }

    // Returns the active configuration instance or triggers a load call if empty
    public static Config get() {
        if (config == null) {
            load();
        }
        return config;
    }
}
