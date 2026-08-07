package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.ShadowCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;

public class ShadowCorePassiveLogic {

    public static void handleDamageReveal(
            ServerPlayer player,
            DamageSource source
    ) {

        if (source.is(DamageTypeTags.IS_FALL)) {
            return;
        }

        if (source.is(DamageTypeTags.IS_FIRE)) {
            return;
        }

        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return;
        }

        if (source.getEntity() instanceof ServerPlayer) {
            ShadowCore.revealPlayer(player);
        }
    }
}