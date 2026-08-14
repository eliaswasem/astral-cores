package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

public class LeviathanCoreLogic {
    public static void applyPassive(ServerPlayer player) {
        if (player.isInWater()) {
            Effects.applyEffect(player, MobEffects.DOLPHINS_GRACE, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.WATER_BREATHING, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.CONDUIT_POWER, 20, 2, false, false, false);
        }
        if (player.isInWaterOrRain()) {
            Effects.applyEffect(player, MobEffects.STRENGTH,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.SPEED,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.REGENERATION,20, 1, false, false, false);
        }
    }
}
