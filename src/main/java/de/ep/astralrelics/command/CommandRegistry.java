package de.ep.astralrelics.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegistry {

    // Central registration hook for all mod commands
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Register specific command trees sequentially
            //AstralRelicCommand.register(dispatcher);
            TrustCommand.register(dispatcher);

        });
    }
}
