package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CoreInteractLogic {

    /**
     * Handles the logical checks and profile modifications when a player right-clicks with a core.
     * Evaluates the player's sneaking state to determine if the core belongs in the left or right slot.
     */
    public static InteractionResult executeEquip(ServerPlayer player, ItemStack stack, Core core, InteractionHand hand) {
        /* Fetches the current persistent data instance associated with the active player from the SQLite database cache */
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        boolean assignToLeft = player.isShiftKeyDown();

        if (assignToLeft) {
            if (data.getLeftCore() != null) {
                player.sendSystemMessage(Component.literal("§cYour left core slot is already occupied!"));
                return InteractionResult.FAIL;
            }
            data.setLeftCore(core.getType());
            player.sendSystemMessage(Component.literal("§aSuccessfully bound " + core.getName() + " to your left profile slot."));
        } else {
            if (data.getRightCore() != null) {
                player.sendSystemMessage(Component.literal("§cYour right core slot is already occupied!"));
                return InteractionResult.FAIL;
            }
            data.setRightCore(core.getType());
            player.sendSystemMessage(Component.literal("§aSuccessfully bound " + core.getName() + " to your right profile slot."));
        }

        /* Permanently shrinks the physical in-game inventory item stack asset unless creative-mode infinite-building is active */
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        /* Notifies the client networking subsystem that the hand transaction succeeded and inventory states updated */
        return InteractionResult.SUCCESS;
    }
}
