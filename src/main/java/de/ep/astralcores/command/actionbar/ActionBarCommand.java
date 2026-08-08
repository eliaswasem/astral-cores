package de.ep.astralcores.command.actionbar;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralcores.playerdata.PlayerData.ActionBarMode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommand {

    // Register the command literal layout into the Brigadier tree structure
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("actionbar")
                .then(Commands.literal("text")
                        .executes(context -> route(context, ActionBarMode.TEXT)))
                .then(Commands.literal("icon")
                        .executes(context -> route(context, ActionBarMode.ICON)))
        );
    }

    // Resolve the executing entity context and pass it down to the logic layer along with the mode
    private static int route(CommandContext<CommandSourceStack> context, ActionBarMode mode) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        // Redirect execution to the action bar command logic layer
        return ActionBarCommandLogic.execute(context.getSource(), player, mode);
    }
}
