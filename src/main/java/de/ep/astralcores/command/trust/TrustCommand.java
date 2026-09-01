package de.ep.astralcores.command.trust;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class TrustCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("trust")
                        .requires(
                                source ->
                                        source.getEntity()
                                                instanceof ServerPlayer
                        )
                        .then(
                                Commands.argument(
                                                "player",
                                                EntityArgument.player()
                                        )
                                        .executes(
                                                TrustCommandLogic::execute
                                        )
                        )
        );
    }
}
