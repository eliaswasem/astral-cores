package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.playerdata.PlayerDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import java.io.File;

public class ServerLifecycleEventsListener {

    public static void register() {

        // SERVER STARTING: Runs when the world folder becomes active
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {

            /* Cache the server instance statically inside the main class container for true O(1) lookups */
            AstralCores.setServer(server);

            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();

            // Spin up the SQLite connection pool safely
            AstralCores.PLAYER_DATA = new PlayerDataManager(worldDir);

            ConfigManager.load();
        });

        // SERVER STOPPING: Runs on server shutdowns, restarts, or crashes
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            // Backup all currently connected online player data profiles
            if (server != null && server.getPlayerList() != null && AstralCores.PLAYER_DATA != null) {
                server.getPlayerList().getPlayers().forEach(player -> {
                    AstralCores.PLAYER_DATA.save(player);
                });
            }

            // Terminate the physical SQLite file connection to avoid stream locks
            if (AstralCores.PLAYER_DATA != null) {
                AstralCores.PLAYER_DATA.closeConnection();
            }
        });
    }
}
