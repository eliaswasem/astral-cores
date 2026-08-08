package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.ShadowCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;

public class ShadowCorePassiveLogic {

    // Evaluates if taking damage should reveal a player hidden by the shadow core
    public static void handleDamageReveal(
            ServerPlayer player,
            DamageSource source
    ) {

        // Bypasses the reveal check if the damage was caused by falling
        if (source.is(DamageTypeTags.IS_FALL)) {
            return;
        }

        // Bypasses the reveal check if the damage was caused by fire
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return;
        }

        // Bypasses the reveal check if the damage was caused by drowning
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return;
        }

        // Reveals the player if the damage source was another player
        if (source.getEntity() instanceof ServerPlayer) {
            ShadowCore.revealPlayer(player);
        }
    }
}
