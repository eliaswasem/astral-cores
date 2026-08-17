package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CoreInteractLogic {

    // Equips a custom core into the player profile slot when right-clicked
    public static InteractionResult executeEquip(ServerPlayer player, ItemStack stack, Core core, InteractionHand hand) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        // Stops equipment if the single profile slot is already full
        if (data.getEquippedCore() != null) {
            player.sendSystemMessage(Component.literal("§cYour core slot is already occupied!"));
            return InteractionResult.FAIL;
        }

        // Binds the core enum type to the player data profile
        data.setEquippedCore(core.getType());
        player.sendSystemMessage(Component.literal("§aSuccessfully bound " + core.getName() + " to your profile slot."));

        // Updates the action bar display text immediately
        ActionBarManager.tick(player, data);

        // Reduces the item stack count by one if not in creative mode
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
