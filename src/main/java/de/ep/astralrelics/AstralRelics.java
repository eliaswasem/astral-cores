package de.ep.astralrelics;

import de.ep.astralrelics.events.PlayerEvents;
import de.ep.astralrelics.playerdata.PlayerDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstralRelics implements ModInitializer {


	public static final String MOD_ID = "astral_relics";


	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);


	// Global access to player data
	public static PlayerDataManager PLAYER_DATA;



	@Override
	public void onInitialize() {


		LOGGER.info("Initializing Astral Relics");


		// Create managers after the server has loaded the world
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {

			PLAYER_DATA = new PlayerDataManager(
					server.getWorldPath(LevelResource.ROOT).toFile()
			);


			LOGGER.info("PlayerDataManager initialized");

		});


		// Register all events
		PlayerEvents.register();

	}

}