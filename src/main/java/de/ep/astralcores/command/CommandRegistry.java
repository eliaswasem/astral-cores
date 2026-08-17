package de.ep.astralcores.command;

import de.ep.astralcores.command.actionbar.ActionBarCommand;
import de.ep.astralcores.command.activate.ActivateCommand;
import de.ep.astralcores.command.core.CoreCommand;
import de.ep.astralcores.command.debug.DebugCommand;
import de.ep.astralcores.command.trust.TrustCommand;
import de.ep.astralcores.command.untrust.UntrustCommand;
import de.ep.astralcores.command.withdraw.WithdrawCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegistry {

    // Central registration hook for all mod commands
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Register specific commands sequentially
            TrustCommand.register(dispatcher);
            UntrustCommand.register(dispatcher);
            CoreCommand.register(dispatcher);
            WithdrawCommand.register(dispatcher);
            ActivateCommand.register(dispatcher);
            ActionBarCommand.register(dispatcher);
            DebugCommand.register(dispatcher);

        });
    }
}
