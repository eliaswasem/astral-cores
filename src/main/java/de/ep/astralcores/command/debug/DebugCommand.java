package de.ep.astralcores.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class DebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("astralcores-debug")
                        .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(Commands.literal("resetCooldowns")
                                .executes(context -> route(DebugCommandLogic.DebugCommandType.COOLDOWN, context))
                        )
        );
    }

    private static int route(DebugCommandLogic.DebugCommandType debugCommandType, CommandContext<CommandSourceStack> context) {
        ServerPlayer player;

        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }

        return DebugCommandLogic.execute(
                context.getSource(),
                player,
                debugCommandType
        );
    }
}
