package de.ep.astralcores.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {

    @Inject(
            method = "hurtEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            ),
            cancellable = true
    )
    private void astralcores$protectTrusted(
            CallbackInfo ci,
            @Local(name = "entity") Entity entity
    ) {

        if (!(entity instanceof ServerPlayer target)) {
            return;
        }

        ServerExplosion explosion =
                (ServerExplosion) (Object) this;

        Entity source =
                explosion.getDirectSourceEntity();

        if (!(source instanceof ServerPlayer owner)) {
            return;
        }

        PlayerData data =
                AstralCores.PLAYER_DATA.get(owner);

        if (data != null &&
                data.isTrusted(target.getUUID())) {

            ci.cancel();
        }
    }
}