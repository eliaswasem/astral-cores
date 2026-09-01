package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BerserkerCoreLogic {

    private static final Set<UUID> activePlayers = new HashSet<>();
    private static final Map<UUID, TickTimer> ragePlayers = new HashMap<>();

    public static boolean allowHealing = false;

    public static void applyPassive(ServerPlayer player) {
        if (player.getHealth() <= 7) {
            Effects.applyEffect(player, MobEffects.STRENGTH, 25, 3);
        }

        activePlayers.add(player.getUUID());
    }

    public static void activate(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());

        // Rage lasts for 1.5 minutes
        ragePlayers.put(player.getUUID(), new TickTimer(1800));

        // All active effects last for 1.5 minutes
        Effects.applyEffect(player, MobEffects.STRENGTH, 1800, 2);
        Effects.applyEffect(player, MobEffects.SPEED, 1800, 2);
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 1800, 1);
    }

    public static void tick() {
        if (ragePlayers.isEmpty()) return;

        ragePlayers.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        activePlayers.remove(player.getUUID());
        ragePlayers.remove(player.getUUID());
    }

    public static void handleBloodlust(ServerPlayer victim, DamageSource source) {
        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer killer
                && activePlayers.contains(killer.getUUID())) {

            killer.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0f,
                    1.2f
            );

            Effects.applyEffect(killer, MobEffects.SPEED, 200, 2);
            Effects.applyEffect(killer, MobEffects.STRENGTH, 200, 3);

            allowHealing = true;
            killer.heal(4.0f);
            allowHealing = false;
        }
    }

    public static boolean isInRage(ServerPlayer player) {
        return ragePlayers.containsKey(player.getUUID());
    }
}
