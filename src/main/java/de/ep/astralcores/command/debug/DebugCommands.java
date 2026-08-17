package de.ep.astralcores.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class DebugCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("astral")
                        .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(Commands.literal("debug")
                                .then(Commands.literal("resetCooldowns")
                                        .executes(DebugCommands::route)
                                )
                        )
        );
    }

    private static int route(
            com.mojang.brigadier.context.CommandContext<CommandSourceStack> context
    ) {
        ServerPlayer player;

        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }

        return DebugCommandsLogic.execute(
                context.getSource(),
                player
        );
    }
}