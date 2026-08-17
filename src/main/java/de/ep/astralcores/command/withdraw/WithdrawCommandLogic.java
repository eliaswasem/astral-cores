package de.ep.astralcores.command.withdraw;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
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

    // Removes the equipped core from the player slot and converts it back into an item stack
    public static int execute(CommandSourceStack source, ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        CoreType targetedType = data.getEquippedCore();
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYour equipment slot is currently empty."));
            return 0;
        }

        Core core = CoreRegistry.get(targetedType).orElse(null);
        if (core == null) {
            source.sendFailure(Component.literal("§cCritical: Stored core type mapping resolution failure."));
            return 0;
        }

        // Cleans up passive buffs or modifiers before the core gets unequipped
        core.onRemoved(player);

        // Generates the physical item stack for the core item
        ItemStack coreStack = CoreFactory.createStack(core);

        // Clears the equipped core reference from the player profile data
        data.setEquippedCore(null);

        // Updates the action bar display text immediately
        ActionBarManager.tick(player, data);

        // Adds the core item to the inventory or drops it on the ground if full
        if (!player.getInventory().add(coreStack)) {
            player.drop(coreStack, false);
        }

        source.sendSuccess(() -> Component.literal("§aSuccessfully withdrew " + core.getName() + " back to your inventory."), true);
        return 1;
    }
}
