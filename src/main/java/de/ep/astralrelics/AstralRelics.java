package de.ep.astralrelics;

import de.ep.astralrelics.commands.trust.TrustCommand;
import de.ep.astralrelics.events.PlayerEvents;
import de.ep.astralrelics.playerdata.PlayerDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AstralRelics implements ModInitializer {

	public static final String MOD_ID = "astral_relics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Global access to player data
	public static PlayerDataManager PLAYER_DATA;
	private TrustCommand trustCommand;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Astral Relics");

		// 1. Fetch paths and initialize the trust database right away
		File configDir = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile();
		File modDataFolder = new File(configDir, "astral-relics");
		this.trustCommand = new TrustCommand(modDataFolder, 5);

		// 2. Register commands immediately so Fabric registers them on time
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			de.ep.astralrelics.commands.TrustCommandRegistry.register(dispatcher, this.trustCommand);
		});

		// 3. Create world-specific managers after the server has loaded the world
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			PLAYER_DATA = new PlayerDataManager(
					server.getWorldPath(LevelResource.ROOT).toFile()
			);
			LOGGER.info("PlayerDataManager initialized");

			// Register all events
			PlayerEvents.register();
		});

		// 4. Safely close the database when the server is stopping
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (this.trustCommand != null) {
				this.trustCommand.close(); // Safely closes the SQLite connection
			}
		});
	}

	// Getter so your relics and events can access it
	public TrustCommand getTrustCommand() {
		return this.trustCommand;
	}
}
