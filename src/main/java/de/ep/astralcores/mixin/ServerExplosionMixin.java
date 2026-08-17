package de.ep.astralcores.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {

    @WrapOperation(
            method = "hurtEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean astralcores$protectTrusted(
            Entity entity,
            ServerLevel level,
            net.minecraft.world.damagesource.DamageSource source,
            float amount,
            Operation<Boolean> original
    ) {
        ServerExplosion explosion = (ServerExplosion) (Object) this;

        if (!(entity instanceof ServerPlayer target)) {
            return original.call(entity, level, source, amount);
        }

        Entity owner = explosion.getDirectSourceEntity();

        if (!(owner instanceof ServerPlayer player)) {
            return original.call(entity, level, source, amount);
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        if (data != null && data.isTrusted(target.getUUID())) {
            return false;
        }

        return original.call(entity, level, source, amount);
    }
}