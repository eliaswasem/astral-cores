package de.ep.astralcores.command.withdraw;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {

    // Registers the base withdraw command into the game command tree
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("withdraw")
                // Runs the command directly when no additional arguments are provided
                .executes(WithdrawCommand::route)
        );
    }

    // Gets the executing player from the command context and routes it forward
    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirects the action to the main withdrawal logic handler
        return WithdrawCommandLogic.execute(context.getSource(), player);
    }
}
