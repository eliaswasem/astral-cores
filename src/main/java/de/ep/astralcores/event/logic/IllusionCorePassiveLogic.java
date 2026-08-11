package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.IllusionCore;
import de.ep.astralcores.util.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import java.util.Random;

public class IllusionCorePassiveLogic {

    // Define the trigger chance (e.g., 20%)
    private static final double TRIGGER_CHANCE = 0.20;
    private static final Random RANDOM = new Random();

    public static boolean handleMirrorImage(ServerPlayer player, DamageSource source) {
        // 1. Check if the player has the core active
        if (!IllusionCore.activePlayers.contains(player)) {
            return true; // Continue normal damage processing
        }

        // 2. Check if the damage source is a player attack
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            return true; // Continue normal damage processing for non-player attacks
        }

        // 3. Check the random chance trigger
        if (RANDOM.nextDouble() >= TRIGGER_CHANCE) {
            return true; // Chance failed, continue normal damage processing
        }

        // 4. Trigger the Mirror Image Effect
        if (player.getLastAttacker() instanceof ServerPlayer attacker) {
            Effects.applyEffect(attacker, MobEffects.BLINDNESS, 5, 255, false, false, false);
            attacker.sendSystemMessage(Component.literal("You are distracted").withStyle(ChatFormatting.WHITE));
        }
        // 5. Decide outcome
        // Return false if the illusion completely dodges the attack
        // Return true if the attack still hits but the attacker is blinded
        return false;
    }
}