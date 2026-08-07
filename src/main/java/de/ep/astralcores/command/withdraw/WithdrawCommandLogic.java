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
     * Extracts a core from the player's data slot and turns it back into an item.
     * Cleans up all passive attributes and effects instantly.
     */
    public static int execute(CommandSourceStack source, ServerPlayer player, boolean isLeftSlot) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        CoreType targetedType = isLeftSlot ? data.getLeftCore() : data.getRightCore();
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYour selected slot is currently empty."));
            return 0;
        }

        Core core = CoreRegistry.get(targetedType).orElse(null);
        if (core == null) {
            source.sendFailure(Component.literal("§cCritical: Stored core type mapping resolution failure."));
            return 0;
        }

        // Wipe passive status effects and attribute modifiers before removing the core
        core.onRemoved(player);

        ItemStack coreStack = CoreFactory.createStack(core);

        // Clear the player's core slot
        if (isLeftSlot) {
            data.setLeftCore(null);
        } else {
            data.setRightCore(null);
        }

        // Put the core item into the inventory, or drop it at their feet if full
        if (!player.getInventory().add(coreStack)) {
            player.drop(coreStack, false);
        }

        // ActionBarUpdater.update(player);

        source.sendSuccess(() -> Component.literal("§aSuccessfully withdrew " + core.getName() + " back to your inventory."), true);
        return 1;
    }
}
