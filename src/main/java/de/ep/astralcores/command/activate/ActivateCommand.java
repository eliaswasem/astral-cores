package de.ep.astralcores.command.activate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommand {

    /**
     * Registers the structural literal format tree for the public activation command framework.
     * Hooks directly into Brigadier to allow triggering slot abilities via chat or keybind macros.
     * This command is fully public and does not require operator or moderator privileges.
     *
     * @param dispatcher The global server command tree manager used to register custom packet arguments.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("activate")
                /* Attaches the execution path targeting the virtual left actionbar container slot */
                .then(Commands.literal("left")
                        .executes(context -> route(context, true)))
                /* Attaches the execution path targeting the virtual right actionbar container slot */
                .then(Commands.literal("right")
                        .executes(context -> route(context, false)))
        );
    }

    /**
     * Resolves the executing player context block and converts the transaction into the logic execution plane.
     *
     * @param context    The Brigadier tracking data containing command sender tags.
     * @param isLeftSlot A binary toggle informing the processing engine whether to select the left or right slot.
     * @return The standard integer code return structure (1 for success, 0 for failure conditions).
     * @throws CommandSyntaxException Thrown automatically if a non-player entity like the console runs the command.
     */
    private static int route(CommandContext<CommandSourceStack> context, boolean isLeftSlot) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        /* Redirects directly into the core activation execution layer */
        return ActivateCommandLogic.execute(context.getSource(), player, isLeftSlot);
    }
}
