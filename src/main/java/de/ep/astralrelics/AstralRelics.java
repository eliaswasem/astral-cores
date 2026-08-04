package de.ep.astralrelics;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstralRelics implements ModInitializer {
	public static final String MOD_ID = "astral-relics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Astral Relics...");

		MainLoop.register();

		LOGGER.info("Astral Relics initialized successfully!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
