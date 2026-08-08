package de.ep.astralcores.command.activate;

import de.ep.astralcores.manager.CoreActivateManager;
import de.ep.astralcores.manager.CoreActivateManager.ActivationResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommandLogic {

    // Process the core activation mechanics through the global execution manager
    public static int execute(CommandSourceStack source, ServerPlayer player) {
        // Delegate all execution checks and effects to the standalone activation manager
        ActivationResult result = CoreActivateManager.attemptActivation(player);

        // If the manager declined the execution, feed the message back to the sender
        if (!result.isSuccess()) {
            source.sendFailure(result.errorMessage());
            return 0;
        }

        return 1;
    }
}
