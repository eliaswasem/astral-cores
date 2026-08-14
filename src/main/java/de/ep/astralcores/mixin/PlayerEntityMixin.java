package de.ep.astralcores.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.ep.astralcores.core.cores.logic.MagnetCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @Inject(
            method = "attack(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"
            )
    )
    private void astralcores$onSuccessfulAttack(
            Entity entity,
            CallbackInfo ci,
            @Local(name = "damageSource") DamageSource damageSource,
            @Local(name = "criticalAttack") boolean criticalAttack
    ) {
        if (!((Object) this instanceof ServerPlayer attacker)) {
            return;
        }

        if (!(entity instanceof ServerPlayer victim)) {
            return;
        }

        if (!criticalAttack && !damageSource.is(DamageTypes.MACE_SMASH)) {
            return;
        }

        MagnetCoreLogic.executeMagneticDisarm(attacker, victim);
    }
}