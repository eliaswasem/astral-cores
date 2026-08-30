package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.playerdata.PlayerDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;
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

        // Saves all player data before the database is finally closed.
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (AstralCores.PLAYER_DATA == null) {
                return;
            }

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                AstralCores.PLAYER_DATA.save(player);
            }
        });

        // The server has completely stopped.
        // At this point no player disconnect saves should still be running.
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (AstralCores.PLAYER_DATA != null) {
                AstralCores.PLAYER_DATA.closeConnection();
            }
        });
    }
}
