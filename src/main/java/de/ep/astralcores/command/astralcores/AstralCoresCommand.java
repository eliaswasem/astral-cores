package de.ep.astralcores.command.astralcores;

import com.mojang.brigadier.CommandDispatcher;
import de.ep.astralcores.command.astralcores.AstralCoresCommandLogic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;

public class AstralCoresCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("astralcores")
                        .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))

                        .then(
                                Commands.literal("debug")
                                        .then(
                                                Commands.literal("resetCooldowns")
                                                        .executes(AstralCoresCommandLogic::resetCooldowns
                                                        )
                                        )
                        )

                        .then(
                                Commands.literal("place")
                                        .then(
                                                Commands.literal("altar")
                                                        .then(
                                                                Commands.argument(
                                                                                "pos",
                                                                                BlockPosArgument.blockPos()
                                                                        )
                                                                        .executes(AstralCoresCommandLogic::placeAltar
                                                                        )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("remove")
                                        .then(
                                                Commands.literal("altar")
                                                        .executes(AstralCoresCommandLogic::removeAltar)
                                        )
                        )
        );
    }
}