package de.ep.astralcores.mixin;

import de.ep.astralcores.core.cores.logic.BerserkerCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void astralcores$disableRageHealing(float amount, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer serverPlayer) {
            // Cancel healing if the player is in rage and it is not triggered by the mod
            if (BerserkerCoreLogic.isInRage(serverPlayer) && !BerserkerCoreLogic.allowHealing) {
                ci.cancel();
            }
        }
    }
}
