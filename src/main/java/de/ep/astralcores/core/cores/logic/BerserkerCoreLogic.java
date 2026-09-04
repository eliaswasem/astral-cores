package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BerserkerCoreLogic {

    private static final Map<UUID, TickTimer> ragePlayers = new HashMap<>();

    private static final Identifier RAGE_DAMAGE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(
                    "astralcores",
                    "berserker_rage_damage"
            );

    private static final double RAGE_DAMAGE_BONUS = 0.15;

    public static boolean allowHealing = false;

    public static void applyPassive(ServerPlayer player) {
        if (player.getHealth() <= 7) {
            Effects.applyEffect(player, MobEffects.STRENGTH, 25, 3);
        }
    }

    public static CoreActivationResult activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return CoreActivationResult.FAILED;
        }

        player.setHealth(player.getMaxHealth());

        // Rage lasts for 1.5 minutes.
        ragePlayers.put(
                player.getUUID(),
                new TickTimer(1800)
        );

        // +20% melee damage while Rage is active.
        addRageDamageModifier(player);

        // Active effects last for 1.5 minutes.
        Effects.applyEffect(player, MobEffects.STRENGTH, 1800, 2);
        Effects.applyEffect(player, MobEffects.SPEED, 1800, 2);
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 1800, 1);

        player.sendSystemMessage(
                Component.literal("You entered Rage Mode!")
                        .withStyle(ChatFormatting.GREEN)
        );

        return CoreActivationResult.EXECUTED;
    }

    public static void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        TickTimer timer = ragePlayers.get(uuid);

        if (timer == null) {
            return;
        }

        if (!player.isAlive() || player.isRemoved()) {
            cleanup(player);
            return;
        }

        if (timer.tick()) {
            removeRageDamageModifier(player);
            ragePlayers.remove(uuid);
        }
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        ragePlayers.remove(player.getUUID());
        removeRageDamageModifier(player);
    }

    private static void addRageDamageModifier(ServerPlayer player) {
        AttributeInstance attribute =
                player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attribute != null
                && !attribute.hasModifier(RAGE_DAMAGE_MODIFIER_ID)) {

            attribute.addTransientModifier(
                    new AttributeModifier(
                            RAGE_DAMAGE_MODIFIER_ID,
                            RAGE_DAMAGE_BONUS,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
    }

    private static void removeRageDamageModifier(ServerPlayer player) {
        AttributeInstance attribute =
                player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attribute != null
                && attribute.hasModifier(RAGE_DAMAGE_MODIFIER_ID)) {

            attribute.removeModifier(RAGE_DAMAGE_MODIFIER_ID);
        }
    }

    public static void handleBloodlust(
            ServerPlayer victim,
            DamageSource source
    ) {
        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer killer
                && AstralCores.PLAYER_DATA
                .get(killer)
                .getEquippedCore() == CoreType.BERSERKER_CORE) {

            killer.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0f,
                    1.2f
            );

            Effects.applyEffect(
                    killer,
                    MobEffects.SPEED,
                    200,
                    2
            );

            Effects.applyEffect(
                    killer,
                    MobEffects.STRENGTH,
                    200,
                    3
            );

            allowHealing = true;
            killer.heal(4.0f);
            allowHealing = false;
        }
    }

    public static boolean isInRage(ServerPlayer player) {
        return ragePlayers.containsKey(player.getUUID());
    }
}