package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class IllusionCoreLogic {

    private static final double TRIGGER_CHANCE = 0.20;

    private static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public static void applyPassive(ServerPlayer player) {
        activePlayers.add(player);
    }

    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
    }

    /**
     * Handles the passive Mirror Image effect.
     *
     * @return true if normal damage processing should continue,
     *         false if the attack should be cancelled.
     */
    public static boolean handleMirrorImage(
            ServerPlayer player,
            DamageSource source
    ) {
        if (!activePlayers.contains(player)) {
            return true;
        }

        // Mirror Image only reacts to direct player attacks.
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            return true;
        }

        // 20% chance to trigger.
        if (player.getRandom().nextDouble() >= TRIGGER_CHANCE) {
            return true;
        }

        // Find the attacker directly from the damage source.
        if (source.getEntity() instanceof ServerPlayer attacker) {

            Effects.applyEffect(
                    attacker,
                    MobEffects.BLINDNESS,
                    5,
                    255,
                    false,
                    false,
                    false
            );

            attacker.sendSystemMessage(
                    Component.literal("You are distracted!")
                            .withStyle(ChatFormatting.WHITE)
            );
        }

        // Attack is completely dodged.
        return false;
    }
}