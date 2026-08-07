package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Mixin targeting RecipeManager to control ingredient validation protocols.
 * Prevents protected items tracked as core fragments from being consumed inside standard crafting systems.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    /**
     * Intercepts recipe checks at the start of execution. Iterates through input arrays
     * and short-circuits execution by returning an empty result if an authorized core element is found.
     *
     * @param recipeType The functional classification category of the matching process.
     * @param recipeInput The physical matrix grid container holding item data elements.
     * @param level The dimensional context framework the validation is executing inside.
     * @param cir The callback tracking reference managing return data injection.
     */
    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private <I extends RecipeInput, T extends net.minecraft.world.item.crafting.Recipe<I>> void blockCoreIngredients(
            RecipeType<T> recipeType,
            I recipeInput,
            Level level,
            CallbackInfoReturnable<Optional<RecipeHolder<T>>> cir) {

        // Optimized primitive loop prevents object allocations on hot-path execution frames
        int size = recipeInput.size();
        for (int i = 0; i < size; i++) {
            if (CoreFactory.getCoreFromItem(recipeInput.getItem(i)).isPresent()) {
                // Short-circuit instantly and reject the recipe
                cir.setReturnValue(Optional.empty());
                return;
            }
        }
    }
}
