package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int age;

    @Inject(method = "tick", at = @At("HEAD"))
    private void preventCoreDespawn(CallbackInfo ci) {
        // Fetch the item stack currently lying on the ground
        ItemStack stack = this.getItem();

        // Check if the item is recognized as a core by our factory
        if (CoreFactory.isCore(stack)) {
            // Freeze the despawn timer by resetting its age to zero
            this.age = 0;
        }
    }
}
