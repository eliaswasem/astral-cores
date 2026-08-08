package de.ep.astralcores.command.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class CoreCommand {

    // Registers the core give command, checks permissions, and generates tab completions from the registry
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("core")
                        // Restricts command access to server operators and moderators
                        .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(
                                Commands.literal("give")
                                        .then(
                                                Commands.argument("target", EntityArgument.player())
                                                        .then(
                                                                Commands.argument("coreId", StringArgumentType.word())
                                                                        // Populates tab suggestions with all registered core id strings
                                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                                CoreRegistry.getAll().keySet(),
                                                                                builder
                                                                        ))
                                                                        .executes(CoreCommand::route)
                                                        )
                                        )
                        )
        );
    }

    // Resolves the targeted player and core id string from command arguments
    private static int route(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        String coreId = StringArgumentType.getString(context, "coreId");

        // Redirects execution to the core command logic layer
        return CoreCommandLogic.execute(
                context.getSource(),
                target,
                coreId
        );
    }
}
