package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Slot.class, ShulkerBoxSlot.class})
public class SlotAndShulkerBoxSlotMixin {

    // Overrides insertion checks for both standard containers and shulker box slots server-side
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void preventCoreContainerPlacement(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {

        // Bypasses the validation if the target slot belongs to the player's personal inventory profile
        if (((Slot) (Object) this).container instanceof Inventory) {
            return;
        }

        // Rejects placement if the item being moved is identified as a registered core module
        if (CoreFactory.getCoreFromItem(stack).isPresent()) {
            cir.setReturnValue(false);
            return;
        }

        // Extracts internal data bundles to verify nested contents for hidden core structures
        BundleContents bundleData = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleData != null) {
            for (var template : bundleData.items()) {
                // Intercepts the action if a player attempts to bypass container blacklists using bundles
                if (CoreFactory.getCoreFromTemplate(template).isPresent()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
