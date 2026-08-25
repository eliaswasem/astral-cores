package de.ep.astralcores.command.actionbar;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralcores.actionbar.ActionBarMode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommand {

    // Registers the actionbar command and its text/icon sub-arguments into the game command tree
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("actionbar")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .then(
                                Commands.literal("text")
                                .executes(context -> route(context, ActionBarMode.TEXT)))
                        .then(
                                Commands.literal("icon")
                                .executes(context -> route(context, ActionBarMode.ICON)))
        );
    }

    // Resolves the executing player from the command context and routes it along with the chosen mode
    private static int route(CommandContext<CommandSourceStack> context, ActionBarMode mode) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirects execution to the action bar command logic layer
        return ActionBarCommandLogic.execute(context.getSource(), player, mode);
    }
}
