package de.ep.astralcores.command.withdraw;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.ActionBarManager;
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

    // Extract the core from the single data slot and convert it back into an item
    public static int execute(CommandSourceStack source, ServerPlayer player) {
        // Fetch cached player profile data
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        // Read the single equipped core slot
        CoreType targetedType = data.getEquippedCore();
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYour equipment slot is currently empty."));
            return 0;
        }

        // Cross-reference with registry to obtain the core instance
        Core core = CoreRegistry.get(targetedType).orElse(null);
        if (core == null) {
            source.sendFailure(Component.literal("§cCritical: Stored core type mapping resolution failure."));
            return 0;
        }

        // Wipe passive status effects and attribute modifiers before removing the core
        core.onRemoved(player);

        // Generate the physical item stack for the core
        ItemStack coreStack = CoreFactory.createStack(core);

        // Clear the single player core slot
        data.setEquippedCore(null);
        // Instantly update Actionbar
        ActionBarManager.tick(player, data);

        // Give the core item to the player or drop it on the ground if inventory is full
        if (!player.getInventory().add(coreStack)) {
            player.drop(coreStack, false);
        }

        source.sendSuccess(() -> Component.literal("§aSuccessfully withdrew " + core.getName() + " back to your inventory."), true);
        return 1;
    }
}
