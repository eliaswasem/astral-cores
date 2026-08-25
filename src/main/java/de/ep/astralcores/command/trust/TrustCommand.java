package de.ep.astralcores.command.trust;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class TrustCommand {

    // Registers the trust command and its player argument into the game command tree
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("trust")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(
                        Commands.argument("player", EntityArgument.player())
                        .executes(TrustCommand::route)));
    }

    // Resolves both the executing player and the target player from the command arguments
    private static int route(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");

        // Redirects the action to the trust logic handler
        return TrustCommandLogic.execute(context.getSource(), player, target);
    }
}
