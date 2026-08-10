package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    // Injects into the recipe checking logic before standard matching begins
    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private <I extends RecipeInput, T extends Recipe<I>> void blockCoreIngredients(
            RecipeType<T> recipeType,
            I recipeInput,
            Level level,
            CallbackInfoReturnable<Optional<RecipeHolder<T>>> cir) {

        // Loops through the crafting grid input slots efficiently without extra allocations
        int size = recipeInput.size();
        for (int i = 0; i < size; i++) {
            // Rejects the entire recipe if any ingredient is recognized as a mod core item
            if (CoreFactory.isCore(recipeInput.getItem(i))) {
                cir.setReturnValue(Optional.empty());
                return;
            }
        }
    }
}
