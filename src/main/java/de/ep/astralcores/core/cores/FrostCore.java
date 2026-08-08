package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
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
                        "§b[Active: Frost Lock]"
                ),
                10008,
                0,
                0,
                "Frost Lock",
                "Frost Aura"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        ServerLevel level = player.level();
        double maxRange = 8.0;

        AABB searchBox = player.getBoundingBox().inflate(maxRange);

        List<LivingEntity> nearbyEnemies = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive()
        );

        for (LivingEntity enemy : nearbyEnemies) {

            if (data != null && data.isTrusted(enemy.getUUID())){
                continue;
            }
            double distance = player.distanceTo(enemy);

            if (distance <= maxRange) {
                int effectLevel;

                if (distance <= 3.5) {
                    effectLevel = 3; // Slowness III
                } else if (distance <= 5.0) {
                    effectLevel = 2; // Slowness II
                } else {
                   effectLevel = 1; // Slowness I
                }
                Effects.applyEffect(player, MobEffects.SLOWNESS, 40, effectLevel, false, false, false);
            }
        }
    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
