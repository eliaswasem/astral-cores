package de.ep.astralcores.command.untrust;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class UntrustCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("untrust")
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
                                                UntrustCommandLogic::untrust
                                        )
                        )
        );
    }
}