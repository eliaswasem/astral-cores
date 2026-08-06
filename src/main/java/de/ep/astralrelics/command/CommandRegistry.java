package de.ep.astralrelics.command;

import de.ep.astralrelics.command.activate.ActivateCommand;
import de.ep.astralrelics.command.relic.RelicCommand;
import de.ep.astralrelics.command.trust.TrustCommand;
import de.ep.astralrelics.command.untrust.UntrustCommand;
import de.ep.astralrelics.command.withdraw.WithdrawCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegistry {

    // Central registration hook for all mod commands
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Register specific command trees sequentially
            //AstralRelicCommand.register(dispatcher);
            TrustCommand.register(dispatcher);
            UntrustCommand.register(dispatcher);
            RelicCommand.register(dispatcher);
            WithdrawCommand.register(dispatcher);
            ActivateCommand.register(dispatcher);

        });
    }
}
