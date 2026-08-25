package de.ep.astralcores.command.astralcores;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.ep.astralcores.structure.StructureRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
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
                                                .executes(context -> route(AstralCoresCommandLogic.AstralCoresCommandType.DEBUG_COOLDOWN, context))
                                        )
                        )
                        .then(
                                Commands.literal("structure")
                                        .then(
                                                Commands.literal("place")
                                                        .then(
                                                                Commands.argument(
                                                                                "structureType",
                                                                                IdentifierArgument.id()
                                                                        )
                                                                        .suggests((context, builder) ->
                                                                                SharedSuggestionProvider.suggest(
                                                                                        StructureRegistry.getAll(),
                                                                                        builder
                                                                                )
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "pos",
                                                                                                BlockPosArgument.blockPos()
                                                                                        )
                                                                                        .executes(context ->
                                                                                                route(
                                                                                                        AstralCoresCommandLogic.AstralCoresCommandType.STRUCTURE_PLACE,
                                                                                                        context
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    private static int route(AstralCoresCommandLogic.AstralCoresCommandType astralCoresCommandType, CommandContext<CommandSourceStack> context) {
        return AstralCoresCommandLogic.execute(
                astralCoresCommandType,
                context
        );
    }
}
