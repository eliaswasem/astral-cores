package de.ep.astralcores.command.activate;

import de.ep.astralcores.manager.CoreActivateManager;
import de.ep.astralcores.manager.CoreActivateManager.ActivationResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommandLogic {

    // Attempts to activate the player equipped core and handles the result
    public static int execute(CommandSourceStack source, ServerPlayer player) {
        // Delegates all activation checks and ability triggers to the activate manager
        ActivationResult result = CoreActivateManager.attemptActivation(player);

        // Sends the error message back to the player if the activation failed
        if (!result.isSuccess()) {
            source.sendFailure(result.errorMessage());
            return 0;
        }

        return 1;
    }
}
