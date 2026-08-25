package de.ep.astralcores.command.activate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommand {

    // Registers the base activate command into the game command tree
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("activate")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                // Runs the command directly when no additional arguments are provided
                .executes(ActivateCommand::route)
        );
    }

    // Gets the executing player from the command context and routes it forward
    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirects the action to the main activation command logic handler
        return ActivateCommandLogic.execute(context.getSource(), player);
    }
}
