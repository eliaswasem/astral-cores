package de.ep.astralcores.command.activate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommand {

    // Register the command literal layout into the Brigadier tree structure
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("activate")
                // Execute directly on the base command without left/right sub-arguments
                .executes(ActivateCommand::route)
        );
    }

    // Resolve the executing entity context and pass it down to the logic layer
    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirect execution to the core activation logic layer
        return ActivateCommandLogic.execute(context.getSource(), player);
    }
}
