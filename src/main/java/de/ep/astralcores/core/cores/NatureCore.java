package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.BiomeUtils;
import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;



public class NatureCore extends Core {

    public NatureCore() {
        super(
                CoreType.NATURE_CORE,
                "§2Nature Core",
                Items.SLIME_BALL,
                List.of(
                        "§2[Active: Root Trap]"
                ),
                10012,
                0,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {

        if (BiomeUtils.isInNatureBiome(player)) {
            Effects.applyEffect(player, MobEffects.REGENERATION, 25, 1);
            Effects.applyEffect(player, MobEffects.SPEED, 25, 1);
        }
    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
