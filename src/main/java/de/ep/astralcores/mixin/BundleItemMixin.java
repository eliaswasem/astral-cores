package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    // Intercepts moving an item onto a bundle slot to prevent direct core insertion inside external containers
    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void preventCoreInsertionInContainerBundles(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem, CallbackInfoReturnable<Boolean> cir) {
        // Allows insertion if the target container slot resides inside the player's personal inventory profile
        if (slot.container instanceof Inventory) {
            return;
        }

        // Rejects the interaction if the item stack trying to enter the container bundle is a registered core
        if (CoreFactory.getCoreFromItem(other).isPresent()) {
            cir.setReturnValue(false);
        }
    }
}
