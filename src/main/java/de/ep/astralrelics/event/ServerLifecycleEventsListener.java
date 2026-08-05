package de.ep.astralrelics.event;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import java.io.File;

// Unique class name to prevent compilation conflicts with the Fabric import
public class ServerLifecycleEventsListener {

    public static void register() {

        // SERVER STARTING: Runs when the world folder becomes active
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();

            // Spin up the SQLite connection pool safely
            AstralRelics.PLAYER_DATA = new PlayerDataManager(worldDir);
        });

        // SERVER STOPPING: Runs on server shutdowns, restarts, or crashes
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            // Backup all currently connected online player data profiles
            server.getPlayerList().getPlayers().forEach(player -> {
                AstralRelics.PLAYER_DATA.save(player);
            });

            // Terminate the physical SQLite file connection to avoid stream locks
            AstralRelics.PLAYER_DATA.closeConnection();
        });
    }
}
