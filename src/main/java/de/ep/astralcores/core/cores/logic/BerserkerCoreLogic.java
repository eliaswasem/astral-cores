package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class BerserkerCoreLogic {

    private static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    private static final Map<ServerPlayer, TickTimer> ragePlayers = new WeakHashMap<>();

    public static boolean allowHealing = false;

    public static void applyPassive(ServerPlayer player) {

        if (player.getHealth() <= 7) {
            Effects.applyEffect(player, MobEffects.STRENGTH, 25, 3);
        }

        // Mark this player as having the core active
        activePlayers.add(player);
    }

    public static void activate(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());

        // Assign a 10-second timer to the player
        ragePlayers.put(player, new TickTimer(200));

        Effects.applyEffect(player, MobEffects.STRENGTH, 200, 1);
        Effects.applyEffect(player, MobEffects.SPEED, 1800, 1);
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 1800, 0);
    }

    public static void tickRageTimers() {
        if (ragePlayers.isEmpty()) return;

        // Tick down every player's timer and remove them when finished
        ragePlayers.entrySet().removeIf(entry -> {
            boolean isFinished = entry.getValue().tick();
            if (isFinished) {
                ServerPlayer player = entry.getKey();
                player.removeEffect(MobEffects.STRENGTH);
            }
            return isFinished;
        });
    }

    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
        ragePlayers.remove(player);
    }

    public static void handleBloodlust(ServerPlayer victim, DamageSource source) {

        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer killer && activePlayers.contains(killer)) {
            // Send a sound
            killer.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 1.2f);
            // Apply Effects using your utility
            Effects.applyEffect(killer, MobEffects.SPEED, 200, 2);
            Effects.applyEffect(killer, MobEffects.STRENGTH, 200, 2);

            // Allow mod healing temporarily
            allowHealing = true;
            // Heal the killer (2 hearts)
            killer.heal(4.0f);
            allowHealing = false;
        }
    }

    public static boolean isInRage(ServerPlayer player) {
        return ragePlayers.containsKey(player);
    }
}
