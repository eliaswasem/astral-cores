package de.ep.astralcores.command.withdraw;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {

    // Register the command literal layout into the Brigadier tree structure
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("withdraw")
                // Execute directly on the base command without left/right sub-arguments
                .executes(WithdrawCommand::route)
        );
    }

    // Resolve the executing entity context and pass it down to the logic layer
    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirect execution to the core withdrawal logic layer
        return WithdrawCommandLogic.execute(context.getSource(), player);
    }
}
