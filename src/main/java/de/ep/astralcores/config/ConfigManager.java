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

    private static final File configFile = FabricLoader.getInstance().getConfigDir()
            .resolve("astralcores").resolve("config.json5").toFile();

    private static final Jankson jankson = Jankson.builder().build();

    // The static holder for your Config instance object
    private static Config config;

    /**
     * Reads configuration contents from disk and maps them directly to the static instance.
     * Automatically creates a new default profile if no file exists.
     */
    public static void load() {
        if (!configFile.exists()) {
            config = new Config();
            save();
            return;
        }
        try {
            JsonObject jsonObject = jankson.load(configFile);
            config = jankson.fromJson(jsonObject, Config.class);
        } catch (IOException | SyntaxError e) {
            AstralCores.LOGGER.error("Failed to parse or read astralcores config.json5 file! Reverting to defaults.", e);
            config = new Config();
        }
    }

    /**
     * Serializes current memory instance values out into a structured text document layout.
     * Preserves descriptive comment flags mapped within the object models.
     */
    public static void save() {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            // If save is called before load, ensure config isn't null
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

    /**
     * Retrieves the active configuration data layer instance wrapper block.
     * Lazily triggers an automatic parsing chain sequence run if memory is empty.
     */
    public static Config get() {
        if (config == null) {
            load();
        }
        return config;
    }
}
