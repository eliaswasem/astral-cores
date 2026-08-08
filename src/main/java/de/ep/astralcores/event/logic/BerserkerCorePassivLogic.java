package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.BerserkerCore;
import de.ep.astralcores.util.Effects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

public class BerserkerCorePassivLogic {
    public static void handleBloodlust(ServerPlayer victim, DamageSource source) {

        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer killer && BerserkerCore.activePlayers.containsKey(killer)) {
            handleBerserkerKill(killer);
        }

    }
    private static void handleBerserkerKill(ServerPlayer killer) {
        // Send a message
        killer.sendSystemMessage(Component.literal("§c§lBLOODLUST TRIGGERED! §r§7Rage consumes you."));
        // Apply Effects using your utility
        Effects.applyEffect(killer, MobEffects.SPEED, 200, 2 );
        Effects.applyEffect(killer, MobEffects.STRENGTH, 200, 2 );
        // Heal the killer (2 hearts)
        killer.heal(4.0f);
    }

}

