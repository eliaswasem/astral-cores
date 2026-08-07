package de.ep.astralcores.mixin;

import de.ep.astralcores.core.cores.ShadowCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {

    /**
     * Intercepts the core artificial intelligence target-setting pipeline.
     * Denies mob aggro vectors if the incoming target is a player hidden by the shadow core.
     */
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void interceptShadowTargeting(LivingEntity target, CallbackInfo ci) {
        /* Check if the incoming target objective is a real physical ServerPlayer instance */
        if (target instanceof ServerPlayer player) {

            /* Cancel the aggro assignment if the core state tracker confirms the player is hidden */
            if (ShadowCore.isPlayerHidden(player.getUUID())) {

                /* Force-clear the target pipeline internally inside the native mob engine */
                ((Mob) (Object) this).setTarget(null);

                /* Abort execution to prevent the vanilla pathfinder from tracking this entity */
                ci.cancel();
            }
        }
    }
}
