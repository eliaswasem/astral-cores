package de.ep.astralcores.command.relic;

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

public class RelicCommand {

    /* Registers the structural layout tree and filters matching tab suggestions for active operators */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("relic")
                        .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(
                                Commands.literal("give")
                                        .then(
                                                Commands.argument("target", EntityArgument.player())
                                                        .then(
                                                                Commands.argument("astralId", StringArgumentType.word())
                                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                                CoreRegistry.getAll().keySet(),
                                                                                builder
                                                                        ))
                                                                        .executes(RelicCommand::route)
                                                        )
                                        )
                        )
        );
    }

    /* Extracts specific context mapping keys and routes execution directly to the functional layer */
    private static int route(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        String astralId = StringArgumentType.getString(context, "astralId");

        return RelicCommandLogic.execute(
                context.getSource(),
                target,
                astralId
        );
    }
}
