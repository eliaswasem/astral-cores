package de.ep.astralcores.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.cores.logic.NatureCoreLogic;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin {

    @WrapOperation(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean astralcores$filterCloudEffect(
            LivingEntity target,
            MobEffectInstance effect,
            Entity source,
            Operation<Boolean> original
    ) {
        AreaEffectCloud cloud = (AreaEffectCloud) (Object) this;

        // Only handle clouds with a ServerPlayer as owner.
        if (!(cloud.getOwner() instanceof ServerPlayer owner)) {
            return original.call(target, effect, source);
        }

        // Protect the owner if they have the Nature Core.
        if (target == owner) {
            if (NatureCoreLogic.hasNatureCore(owner)) {
                return false;
            }

            return original.call(target, effect, source);
        }

        // Protect trusted entities of the cloud owner.
        PlayerData data = AstralCores.PLAYER_DATA.get(owner);

        if (data != null && data.isTrusted(target.getUUID())) {
            return false;
        }

        // Everyone else receives the effect normally.
        return original.call(target, effect, source);
    }
}