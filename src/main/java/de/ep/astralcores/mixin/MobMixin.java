package de.ep.astralcores.mixin;

import de.ep.astralcores.core.cores.logic.ShadowCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {

    // Injects into the start of the mob target assignment method
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void astralcores$interceptShadowTargeting(LivingEntity target, CallbackInfo ci) {
        // Stops execution if the target objective is not a player
        if (target instanceof ServerPlayer player) {

            // Checks if the player is actively hidden by the shadow core state tracker
            if (ShadowCoreLogic.isPlayerHidden(player)) {

                // Clears the target field inside the mob instance
                ((Mob) (Object) this).setTarget(null);

                // Cancels the target assignment update loop
                ci.cancel();
            }
        }
    }
}
