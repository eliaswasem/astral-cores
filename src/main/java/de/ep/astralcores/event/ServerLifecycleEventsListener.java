package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.playerdata.PlayerDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import java.io.File;

public class ServerLifecycleEventsListener {

    // Registers server startup and shutdown lifecycle event listeners
    public static void register() {

        // Runs when the server is starting and the world folder becomes active
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {

            // Saves the server instance statically in the main class
            AstralCores.setServer(server);

            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();

            // Initializes the SQLite data manager using the world directory
            AstralCores.PLAYER_DATA = new PlayerDataManager(worldDir);

            ConfigManager.load();
        });

        // Runs when the server is stopping or shutting down
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {

            // Saves data for all currently online players to the database
            if (server != null && server.getPlayerList() != null && AstralCores.PLAYER_DATA != null) {
                server.getPlayerList().getPlayers().forEach(player -> {
                    AstralCores.PLAYER_DATA.save(player);
                });
            }

            // Closes the active database connection safely
            if (AstralCores.PLAYER_DATA != null) {
                AstralCores.PLAYER_DATA.closeConnection();
            }
        });
    }
}
