package de.ep.astralcores.command.withdraw;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class WithdrawCommandLogic {

    /**
     * Processes the core extraction mechanics to pull an unequipped core out of a data profile back to an item.
     * Inspects the requested slot configuration, verifies presence, pulls details from the central template,
     * clears the field inside the relational SQL database representation, and deploys a newly built factory stack.
     *
     * @param source     The command context wrapper used to echo success or warning messages back down the chat pipeline.
     * @param player     The active ServerPlayer targeted by the context extraction routine who owns the slots.
     * @param isLeftSlot Flag determining whether the active loop should operate on the left or right account data.
     * @return An execution code where 1 represents successful completion and 0 safely signals a failure state.
     */
    public static int execute(CommandSourceStack source, ServerPlayer player, boolean isLeftSlot) {
        /* Fetches the current persistent data instance associated with the active player from the SQLite database cache */
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        /* Reads the current active Enum field state from the selected position in the player data layout model */
        CoreType targetedType = isLeftSlot ? data.getLeftCore() : data.getRightCore();

        /* If the resolved registry slot points to a null reference state, notify the client that nothing can be withdrawn */
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYour selected slot is currently empty."));
            return 0;
        }

        /* Cross-references the active Enum value with the central registry layout map to obtain item data and spell methods */
        Core core = CoreRegistry.get(targetedType).orElse(null);
        if (core == null) {
            source.sendFailure(Component.literal("§cCritical: Stored core type mapping resolution failure."));
            return 0;
        }

        /* Requests the factory class to compile a core item */
        ItemStack coreStack = CoreFactory.createStack(core);

        /* Completely wipes the database cache entry by reverting the specified memory track field configuration back to null */
        if (isLeftSlot) {
            data.setLeftCore(null);
        } else {
            data.setRightCore(null);
        }

        /* Attempts to slip the newly constructed data item directly into the player's primary inventory data arrays */
        if (!player.getInventory().add(coreStack)) {
            /* If the inventory grid arrays are completely saturated, drop the physical asset safely into the world at their feet */
            player.drop(coreStack, false);
        }

        /* Instantly updates the client layout screen before the next global interval tick triggers */
        //ActionBarUpdater.update(player);

        /* Signals back to the sender context tracking link that the operation completed flawlessly */
        source.sendSuccess(() -> Component.literal("§aSuccessfully withdrew " + core.getName() + " back to your inventory."), true);
        return 1;
    }
}
