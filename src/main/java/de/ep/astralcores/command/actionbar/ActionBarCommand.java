package de.ep.astralcores.command.actionbar;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("actionbar")
                        .requires(
                                source ->
                                        source.getEntity()
                                                instanceof ServerPlayer
                        )
                        .then(
                                Commands.literal("text")
                                        .executes(
                                                ActionBarCommandLogic::setText
                                        )
                        )
                        .then(
                                Commands.literal("icon")
                                        .executes(
                                                ActionBarCommandLogic::setIcon
                                        )
                        )
        );
    }
}
