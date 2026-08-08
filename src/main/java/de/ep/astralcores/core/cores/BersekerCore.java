package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.WeakHashMap;

public class BersekerCore extends Core {

    // Track which players currently have this core's passive effect active
    private static final WeakHashMap<ServerPlayer, Boolean> activePlayers = new WeakHashMap<>();

    public BersekerCore() {
        super(
                CoreType.BERSERKER_CORE,
                "§6Berseker Core",
                Items.RABBIT,
                List.of(
                        "§6[Active: Rage Mode]"
                ),
                10011,
                0,
                0,
                "aRage Mode",
                "Bloodlust",
                ""
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        // Mark this player as having the core active
        activePlayers.put(player, true);

        // Register the death event listener
        // Note: In a production mod, you might want to ensure this isn't registered multiple times
        // if applyPassive is called repeatedly, but for a simple Core system, this is standard.
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) -> {

            // 1. Ensure the entity that died is a player
            if (entity instanceof Player victim) {

                // 2. Get the attacker from the damage source
                Entity attacker = source.getEntity();

                // 3. Check if the attacker is a player AND has this core active
                if (attacker instanceof ServerPlayer killer && activePlayers.containsKey(killer)) {

                    // SUCCESS: A player with the Berseker Core killed another player
                    handleBersekerKill(killer, (ServerPlayer) victim);
                }
            }
        });
    }

    @Override
    public void activate(ServerPlayer player) {
        // Activation logic (if any)
    }

    private void handleBersekerKill(ServerPlayer killer, ServerPlayer victim) {
        // 1. Send a message
        killer.sendSystemMessage(Component.literal("§c§lBLOODLUST TRIGGERED! §r§7Rage consumes you."));

        // 2. Apply Effects using your utility
        // Syntax: applyEffect(entity, effect, duration_seconds, amplifier, show_particles, show_icon, persist_on_death)
        // Duration: 10 seconds, Amplifier: 2 (Level 3), No particles, Show icon, Persist on death
        Effects.applyEffect(killer, MobEffects.SPEED, 10, 2, false, true, true);

        // 3. Heal the killer (2 hearts)
        killer.heal(4.0f);
    }
}