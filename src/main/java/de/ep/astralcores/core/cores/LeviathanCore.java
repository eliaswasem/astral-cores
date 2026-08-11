package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;

public class LeviathanCore extends Core {

    public LeviathanCore() {
        super(
                CoreType.LEVIATHAN_CORE,
                "§1Leviathan Core",
                Items.TIPPED_ARROW,
                List.of(
                        "§1[Active: Whirlpool]"
                ),
                10009,
                0,
                0,
                "Whirlpool",
                "Oceanborn",
                "\uE007"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        if (player.isInWater()) {
            Effects.applyEffect(player, MobEffects.DOLPHINS_GRACE, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.WATER_BREATHING, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.CONDUIT_POWER, 20, 2, false, false, false);
        }
        if (player.isInWaterOrRain()) {
            Effects.applyEffect(player, MobEffects.STRENGTH,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.SPEED,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.REGENERATION,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.HASTE,20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.SATURATION,20, 1, false, false, false);
        }
    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
