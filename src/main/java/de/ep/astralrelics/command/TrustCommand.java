package de.ep.astralrelics.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

/**
 * Class handles registration and execution logic for one-way trust protection commands.
 */
public class TrustCommand {

    // Maximum number of trusted players allowed per data profile row
    private static final int MAX_TRUST_LIMIT = 5;

    /**
     * Entrypoint method for building the Brigadier command node tree.
     * Maps syntax patterns to executor methods.
     *
     * @param dispatcher Central command dispatcher instance
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // Literal node base: /trust
        dispatcher.register(Commands.literal("trust")
                // Argument constraint: Expects an online player entity instance
                .then(Commands.argument("player", EntityArgument.player())
                        // Executor callback state: Runs code context with adding flag set to true
                        .executes(context -> handleTrustChange(context, true))));

        // Literal node base: /untrust
        dispatcher.register(Commands.literal("untrust")
                // Argument constraint: Expects an online player entity instance
                .then(Commands.argument("player", EntityArgument.player())
                        // Executor callback state: Runs code context with adding flag set to false
                        .executes(context -> handleTrustChange(context, false))));
    }

    /**
     * Orchestrates list modification checks and structural changes.
     *
     * @param context Brigadier instruction context scope
     * @param isAdding State parameter (true = append element, false = remove element)
     * @return Execution return state flag (1 = Success, 0 = Aborted)
     * @throws CommandSyntaxException Thrown if the command source is not a player entity
     */
    private static int handleTrustChange(CommandContext<CommandSourceStack> context, boolean isAdding) throws CommandSyntaxException {
        // Extraction of the central execution source handle
        CommandSourceStack source = context.getSource();

        // Safely extract the active sender player instance or throw exception if console executed
        ServerPlayer player = source.getPlayerOrException();

        // Extract the online target player instance matching the provided argument string
        ServerPlayer target = EntityArgument.getPlayer(context, "player");

        // Primary identifier key for the command execution sender
        UUID playerUUID = player.getUUID();

        // Primary identifier key for the targeted player element
        UUID targetUUID = target.getUUID();

        // Condition verification: Cancel immediately if a player attempts to target themselves
        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(Component.literal("§cYou cannot trust yourself."));
            return 0;
        }

        // Fetch transient memory data profiles directly from the global SQLite engine cache map
        PlayerData playerData = AstralRelics.PLAYER_DATA.get(player);

        // Branching path handling trust list additions
        if (isAdding) {

            // Check capacity: Validate list bounds against configuration thresholds
            if (playerData.getTrustedPlayers().size() >= MAX_TRUST_LIMIT) {
                source.sendFailure(Component.literal("§cYou have reached your trusted player limit."));
                return 0;
            }

            // Append element to internal list map array tracker
            if (playerData.addTrustedPlayer(targetUUID)) {

                // Write active transient data changes back down onto SQLite files immediately
                AstralRelics.PLAYER_DATA.save(player);

                // Send success notification ONLY to sender confirming target protection status
                source.sendSystemMessage(Component.literal("§aYou now trust " + target.getScoreboardName() + ". They are immune to your relics."));
            } else {
                // Cancel operation notice if element index validation matches a previous insertion
                source.sendSystemMessage(Component.literal("§eYou already trust " + target.getScoreboardName() + "."));
            }

            // Branching path handling trust list removals
        } else {

            // Remove matching element parameter fields from transient target tracking arrays
            if (playerData.removeTrustedPlayer(targetUUID)) {

                // Persist updated structure sets to disk storage
                AstralRelics.PLAYER_DATA.save(player);

                // Output status notification detailing active state breakdown ONLY to sender
                source.sendSystemMessage(Component.literal("§eYou no longer trust " + target.getScoreboardName() + ". They can now take relic damage from you."));
            } else {
                // Return fallback error logs if targeted key lookup failed to trace matching rows
                source.sendFailure(Component.literal("§c" + target.getScoreboardName() + " was not on your trust list."));
            }
        }

        // Return 1 to signify successful loop cycle back to Brigadier engine compiler parameters
        return 1;
    }
}
