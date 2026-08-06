package de.ep.astralcores.command.withdraw;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {

    /**
     * Registers the syntax structural tree mapping left and right sub-arguments to routing links.
     * Hooks directly into Brigadier to construct arguments and maps specific branches to different handlers.
     * This command is fully public and does not require operator or moderator execution privileges.
     *
     * @param dispatcher The global server command tree manager used to register custom packet arguments.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("withdraw")
                /* Attaches the unique directional literal path targeting the virtual left actionbar container slot */
                .then(Commands.literal("left")
                        .executes(context -> route(context, true)))
                /* Attaches the unique directional literal path targeting the virtual right actionbar container slot */
                .then(Commands.literal("right")
                        .executes(context -> route(context, false)))
        );
    }

    /**
     * Resolves the executing player context block and converts the transaction into the logic execution plane.
     * Ensures that the source behind the command packet is a real physical player entity inside a world layer.
     *
     * @param context    The Brigadier tracking data containing command sender tags and environment variables.
     * @param isLeftSlot A binary toggle informing the processing engine whether to select the left or right data array.
     * @return The standard integer code return structure (1 for success, 0 for generic failure conditions).
     * @throws CommandSyntaxException Thrown automatically if a non-player entity like the console runs the command block.
     */
    private static int route(CommandContext<CommandSourceStack> context, boolean isLeftSlot) throws CommandSyntaxException {
        /* Safely unwraps and extracts the ServerPlayer entity context from the incoming command sender source block */
        ServerPlayer player = context.getSource().getPlayerOrException();

        /* Directs the runtime pipeline safely into the operational business logic layer for physical item generation */
        return WithdrawCommandLogic.execute(context.getSource(), player, isLeftSlot);
    }
}
