package de.ep.astralcores.command.activate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommand {

    // Registers the base activate command into the game command tree
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("activate")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                // Runs the command directly when no additional arguments are provided
                .executes(ActivateCommandLogic::execute)
        );
    }
}