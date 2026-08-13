package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class MagnetCore extends Core {

    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public MagnetCore() {
        super(
                CoreType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        "§4[Active: Magnetic Pull]"
                ),
                10012,
                0,
                0,
                "Magnetic Pull",
                "Magnetic Disarm",
                "\uE00C"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {

        activePlayers.add(player);
    }

    @Override
    public void activate(ServerPlayer player) {

    }

    @Override
    public void onRemoved(ServerPlayer player) {

        activePlayers.remove(player);
    }

    public static void executeMagneticDisarm(ServerPlayer attacker, ServerPlayer victim) {
        // The attacker must be the player who currently has the Magnet Core active.
        if (!MagnetCore.activePlayers.contains(attacker)) {
            return;
        }

        // Load the attacker's player data so their trust list can be checked.
        var data = AstralCores.PLAYER_DATA.get(attacker);

        // Players trusted by the Magnet Core owner are not affected by Magnetic Disarm.
        if (data != null && data.isTrusted(victim.getUUID())) {
            return;
        }

        // Magnetic Disarm has a 25% chance to trigger on a qualifying attack.
        if (attacker.getRandom().nextFloat() >= 0.25F) {
            return;
        }

        // Apply heavy Mining Fatigue for 30 ticks (1.5 seconds),
        // effectively preventing the victim from attacking normally.
        Effects.applyEffect(
                victim,
                MobEffects.MINING_FATIGUE,
                30,
                255,
                false,
                false,
                false
        );

        // Inform the victim that their weapon has been magnetized.
        victim.sendSystemMessage(
                Component.literal("Your Weapon is Magnetized")
                        .withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)
        );

        // Create the visual effect around the victim to indicate the disarm.
        spawnWeaponParticles(victim);
    }

    private static void spawnWeaponParticles(ServerPlayer player) {
        ServerLevel level = player.level();

        // Spawn particles slightly in front of the player's view,
        // giving the appearance that the weapon itself is magnetized.
        level.sendParticles(
                ParticleTypes.FIREWORK,
                player.getX() + player.getLookAngle().x * 0.6,
                player.getEyeY() - 0.3 + player.getLookAngle().y * 0.4,
                player.getZ() + player.getLookAngle().z * 0.6,
                90,
                0.1,
                0.1,
                0.1,
                0.0
        );
    }
}
