package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.BiomeUtils;
import de.ep.astralcores.util.CropUtils;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.FoodUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

public final class NatureCoreLogic {

    private NatureCoreLogic() {
    }

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

    public static void activate(ServerPlayer player) {
        // Root Trap currently has no implementation.
    }

    public static void onRemoved(ServerPlayer player) {
        // No persistent state to clean up.
    }

    private static void handleFoodHealing(ServerPlayer player) {
        if (!FoodUtils.isFinishedEating(player)) {
            return;
        }

        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healing = 8.0f;

        player.setHealth(
                Math.min(maxHealth, currentHealth + healing)
        );
    }
}