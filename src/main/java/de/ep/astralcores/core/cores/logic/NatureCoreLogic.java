package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.util.BiomeUtils;
import de.ep.astralcores.util.CropUtils;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.FoodUtils;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.phys.Vec3;


public final class NatureCoreLogic {

    public static void applyPassive(ServerPlayer player) {

        if (!BiomeUtils.isInNatureBiome(player)) {
            return;
        }

        Effects.applyEffect(
                player,
                MobEffects.REGENERATION,
                25,
                1
        );

        Effects.applyEffect(
                player,
                MobEffects.SPEED,
                25,
                1
        );

        CropUtils.growNearbyCrops(
                player,
                4,
                0.05f
        );

        handleFoodHealing(player);
    }

    public static CoreActivationResult activate(ServerPlayer player) {
        ServerLevel level = player.level();
        Vec3 pos = player.position();

        float radius = 4.0f;

        if (BiomeUtils.isInNatureBiome(player)) {
            radius = 6.0f;
        }

        AreaEffectCloud cloud = new AreaEffectCloud(
                level,
                pos.x,
                pos.y + 0.5,
                pos.z
        );

        cloud.setOwner(player);
        cloud.setRadius(radius);
        cloud.setDuration(120);
        cloud.setWaitTime(0);

        cloud.addEffect(
                new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        40,
                        255
                )
        );

        cloud.setCustomParticle(
                ColorParticleOption.create(
                        ParticleTypes.TINTED_LEAVES,
                        0xFF3D7A2E
                )
        );

        level.addFreshEntity(cloud);

        return CoreActivationResult.EXECUTED;
    }

    private static void handleFoodHealing(ServerPlayer player) {
        if (!FoodUtils.isFinishedEating(player)) {
            return;
        }

        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healing = 8.0f;

        player.setHealth(
                Math.min(
                        maxHealth,
                        currentHealth + healing
                )
        );
    }

    public static boolean hasNatureCore(ServerPlayer player) {
        return  (AstralCores.PLAYER_DATA.get(player).getEquippedCore() == CoreType.NATURE_CORE);
    }
}