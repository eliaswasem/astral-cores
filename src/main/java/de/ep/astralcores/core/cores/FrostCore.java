package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FrostCore extends Core {

    public FrostCore() {
        super(
                CoreType.FROST_CORE,
                "§bFrost Core",
                Items.CLAY_BALL,
                List.of(
                        "§b[Passive: Frost Aura]",
                        "§7Emits a slowness aura that gets",
                        "§7stronger the closer enemies are.",
                        "",
                        "§b[Active: Frost Lock]"
                ),
                10008,
                0,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        ServerLevel level = player.level();
        double maxRange = 8.0;

        AABB searchBox = player.getBoundingBox().inflate(maxRange);

        List<LivingEntity> nearbyEnemies = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive()
        );

        for (LivingEntity enemy : nearbyEnemies) {
            double distance = player.distanceTo(enemy);

            if (distance <= maxRange) {
                int amplifier;

                if (distance <= 3.5) {
                    amplifier = 2; // Slowness III
                } else if (distance <= 5.0) {
                    amplifier = 1; // Slowness II
                } else {
                    amplifier = 0; // Slowness I
                }

                enemy.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, amplifier, false, false, true));
            }
        }
    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
