package de.ep.astralrelics.event.logic;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import de.ep.astralrelics.relic.Relic;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class RelicInteractLogic {

    /**
     * Handles the logical checks and profile modifications when a player right-clicks with a relic.
     * Evaluates the player's sneaking state to determine if the relic belongs in the left or right slot.
     */
    public static InteractionResult executeEquip(ServerPlayer player, ItemStack stack, Relic relic, InteractionHand hand) {
        /* Fetches the current persistent data instance associated with the active player from the SQLite database cache */
        PlayerData data = AstralRelics.PLAYER_DATA.get(player);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        boolean assignToLeft = player.isShiftKeyDown();

        if (assignToLeft) {
            if (data.getLeftRelic() != null) {
                player.sendSystemMessage(Component.literal("§cYour left relic slot is already occupied!"));
                return InteractionResult.FAIL;
            }
            data.setLeftRelic(relic.getType());
            player.sendSystemMessage(Component.literal("§aSuccessfully bound " + relic.getName() + " to your left profile slot."));
        } else {
            if (data.getRightRelic() != null) {
                player.sendSystemMessage(Component.literal("§cYour right relic slot is already occupied!"));
                return InteractionResult.FAIL;
            }
            data.setRightRelic(relic.getType());
            player.sendSystemMessage(Component.literal("§aSuccessfully bound " + relic.getName() + " to your right profile slot."));
        }

        /* Permanently shrinks the physical in-game inventory item stack asset unless creative-mode infinite-building is active */
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        /* Notifies the client networking subsystem that the hand transaction succeeded and inventory states updated */
        return InteractionResult.SUCCESS;
    }
}
