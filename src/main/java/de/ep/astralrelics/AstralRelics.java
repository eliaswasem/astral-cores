package de.ep.astralrelics;

import de.ep.astralrelics.command.CommandRegistry;
import de.ep.astralrelics.event.PlayerEvents;
import de.ep.astralrelics.event.ServerLifecycleEventsListener;
import de.ep.astralrelics.playerdata.PlayerDataManager;
import de.ep.astralrelics.relic.RelicRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstralRelics implements ModInitializer {

	public static final String MOD_ID = "astral_relics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Persistent SQLite data processor instance
	public static PlayerDataManager PLAYER_DATA;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AstralRelics Mod...");

		// Initialize server-wide state listeners
		ServerLifecycleEventsListener.register();

		// Initialize individual player state listeners
		PlayerEvents.register();

		// Registers the custom commands
		CommandRegistry.register();

		RelicRegistry.init();

		MainLoop.register();
	}
}
