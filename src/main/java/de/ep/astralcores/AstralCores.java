package de.ep.astralcores;

import de.ep.astralcores.command.CommandRegistry;
import de.ep.astralcores.core.respawn.CoreRespawnDataManager;
import de.ep.astralcores.event.PlayerEventsListener;
import de.ep.astralcores.event.ServerLifecycleEventsListener;
import de.ep.astralcores.playerdata.PlayerDataManager;
import de.ep.astralcores.core.CoreRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstralCores implements ModInitializer {

	public static final String MOD_ID = "astralcores";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static CoreRespawnDataManager CORE_RESPAWN_DATA;

	// Persistent SQLite data processor instance
	public static PlayerDataManager PLAYER_DATA;

	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AstralCores...");
		// Registers server lifecycle event listeners
		ServerLifecycleEventsListener.register();

		// Registers the player event listeners
		PlayerEventsListener.register();

		// Registers the custom commands
		CommandRegistry.register();

		// Initializes core registry
		CoreRegistry.init();

		// Registers the Mainloop
		MainLoop.register();
	}

	public static void setServer(MinecraftServer minecraftServer) {
		server = minecraftServer;
	}

	public static MinecraftServer getServer() {
		return server;
	}
}


