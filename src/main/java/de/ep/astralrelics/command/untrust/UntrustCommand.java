package de.ep.astralrelics.command.untrust;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class UntrustCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register routing syntax layout structure
        dispatcher.register(Commands.literal("untrust")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(UntrustCommand::route)));
    }

    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // Extract command targets and hand off immediately to execution layer
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");

        return UntrustCommandLogic.execute(context.getSource(), player, target);
    }
}
