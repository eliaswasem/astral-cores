package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores; // Generates access point to your static PLAYER_DATA variable
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PhoenixCore extends Core {

    public PhoenixCore() {
        super(
                CoreType.PHOENIX_CORE,
                "§cPhoenix Core",
                Items.MAGMA_CREAM,
                List.of(
                        "§7Forged in cosmic radiation.",
                        "§6[Active: Phoenix Burst]"
                ),
                10003,
                30,
                0,
                "Phoenix Burst",
                "Flameborn"
        );
    }

    /*
     * Continuous passive fire immunity application.
     */
    @Override
    public void applyPassive(ServerPlayer player) {
        // Keeps the fire resistance active seamlessly every tick
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 25, 1);

        // Safely checks if the current server level is the Nether dimension
        if (player.level().dimension().equals(Level.NETHER)) {
            Effects.applyEffect(player, MobEffects.REGENERATION, 25, 1);
        }
    }

    /*
     * Area-of-effect fire ignition ability deployment.
     */
    @Override
    public void activate(ServerPlayer player) {

        ServerLevel level = player.level();

        player.sendSystemMessage(
                Component.literal("§cYou unleashed a" + getActiveAbilityName() + "!")
        );

        Vec3 pos = player.position();

        // Large explosion similar to vanilla, but without destroying blocks
        level.explode(
                player,
                pos.x,
                pos.y,
                pos.z,
                4.0F,
                true,
                Level.ExplosionInteraction.NONE
        );

        // Additional phoenix visual styling
        level.sendParticles(
                ParticleTypes.FLAME,
                pos.x,
                pos.y + 1,
                pos.z,
                200,
                2,
                1.0,
                2,
                0.08
        );

        level.sendParticles(
                ParticleTypes.LAVA,
                pos.x,
                pos.y + 1,
                pos.z,
                80,
                1.33,
                0.66,
                1.33,
                0.05
        );

        // Sound effect played after the explosion
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                1.33F,
                0.8F
        );
    }

}
