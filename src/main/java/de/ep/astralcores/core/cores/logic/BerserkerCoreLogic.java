package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class BerserkerCoreLogic {

    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public static void applyPassive(ServerPlayer player) {

        if (player.getHealth() <= 7) {
            Effects.applyEffect(player, MobEffects.STRENGTH, 25, 3);
        }

        // Mark this player as having the core active
        activePlayers.add(player);
    }

    public static void activate(ServerPlayer player) {
        Effects.applyEffect(player, MobEffects.STRENGTH, 200, 3);
        Effects.applyEffect(player, MobEffects.STRENGTH, 1800, 2);
        Effects.applyEffect(player, MobEffects.SPEED, 1800, 2);
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 1800, 2);
    }

    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
    }

    public static void handleBloodlust(ServerPlayer victim, DamageSource source) {

        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer killer && activePlayers.contains(killer)) {
            // Send a sound
            killer.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 1.2f);
            // Apply Effects using your utility
            Effects.applyEffect(killer, MobEffects.SPEED, 200, 2);
            Effects.applyEffect(killer, MobEffects.STRENGTH, 200, 2);
            // Heal the killer (2 hearts)
            killer.heal(4.0f);
        }
    }
}

