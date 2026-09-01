package de.ep.astralcores.command.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;

import java.util.ArrayList;
import java.util.List;

public class CoreCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(
                Commands.literal("core")
                        .requires(
                                Commands.hasPermission(
                                        Commands.LEVEL_MODERATORS
                                )
                        )

                        // /core give <target> <coreId>
                        .then(
                                Commands.literal("give")
                                        .then(
                                                Commands.argument(
                                                                "target",
                                                                EntityArgument.player()
                                                        )
                                                        .then(
                                                                Commands.argument(
                                                                                "coreId",
                                                                                StringArgumentType.word()
                                                                        )
                                                                        .suggests(
                                                                                (context, builder) ->
                                                                                        SharedSuggestionProvider.suggest(
                                                                                                CoreRegistry.getAll().keySet(),
                                                                                                builder
                                                                                        )
                                                                        )
                                                                        .executes(
                                                                                CoreCommandLogic::give
                                                                        )
                                                        )
                                        )
                        )

                        // /core set <target> <coreId>
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument(
                                                                "target",
                                                                EntityArgument.player()
                                                        )
                                                        .then(
                                                                Commands.argument(
                                                                                "coreId",
                                                                                StringArgumentType.word()
                                                                        )
                                                                        .suggests(
                                                                                (context, builder) ->
                                                                                        SharedSuggestionProvider.suggest(
                                                                                                CoreRegistry.getAll().keySet(),
                                                                                                builder
                                                                                        )
                                                                        )
                                                                        .executes(
                                                                                CoreCommandLogic::set
                                                                        )
                                                        )
                                        )
                        )

                        // /core clear <target>
                        .then(
                                Commands.literal("clear")
                                        .then(
                                                Commands.argument(
                                                                "target",
                                                                EntityArgument.player()
                                                        )
                                                        .executes(
                                                                CoreCommandLogic::clear
                                                        )
                                        )
                        )

                        // /core clearInv <target> <coreId|*>
                        .then(
                                Commands.literal("clearInv")
                                        .then(
                                                Commands.argument(
                                                                "target",
                                                                EntityArgument.player()
                                                        )
                                                        .then(
                                                                Commands.argument(
                                                                                "coreId",
                                                                                StringArgumentType.word()
                                                                        )
                                                                        .suggests(
                                                                                (context, builder) -> {

                                                                                    List<String> suggestions =
                                                                                            new ArrayList<>(
                                                                                                    CoreRegistry
                                                                                                            .getAll()
                                                                                                            .keySet()
                                                                                            );

                                                                                    suggestions.add("*");

                                                                                    return SharedSuggestionProvider.suggest(
                                                                                            suggestions,
                                                                                            builder
                                                                                    );
                                                                                }
                                                                        )
                                                                        .executes(
                                                                                CoreCommandLogic::clearInventory
                                                                        )
                                                        )
                                        )
                        )
        );
    }
}
