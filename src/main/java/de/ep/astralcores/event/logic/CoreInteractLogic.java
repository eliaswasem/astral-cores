package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CoreInteractLogic {

    // Process right-click logic to equip a core into the single available profile slot
    public static InteractionResult executeEquip(ServerPlayer player, ItemStack stack, Core core, InteractionHand hand) {
        // Fetch cached player profile data
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        // Check if the single equipment slot is already occupied
        if (data.getEquippedCore() != null) {
            player.sendSystemMessage(Component.literal("§cYour core slot is already occupied!"));
            return InteractionResult.FAIL;
        }

        // Bind the core to the single equipment slot
        data.setEquippedCore(core.getType());
        player.sendSystemMessage(Component.literal("§aSuccessfully bound " + core.getName() + " to your profile slot."));
        // Instantly update Actionbar
        ActionBarManager.tick(player, data);

        // Consume one item from the stack if the player is not in creative mode
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
