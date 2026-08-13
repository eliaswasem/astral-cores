package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GaleCore extends Core {

    // Players with an active Sonic Dash explosion timer.
    private static final Map<UUID, Integer> explosionTimers = new HashMap<>();

    public GaleCore() {
        super(
                CoreType.GALE_CORE,
                "§7Gale Core",
                Items.BREEZE_ROD,
                List.of(
                        "§f[Active: Sonic Dash]"
                ),
                10005,
                0,
                0,
                "Sonic Dash",
                "Lightfeet",
                "\uE002"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        if (player.isSprinting()) {
            Effects.applyEffect(
                    player,
                    MobEffects.SPEED,
                    25,
                    1
            );
        }
    }

    @Override
    public void activate(ServerPlayer player) {
        Vec3 look = player.getLookAngle();

        player.setDeltaMovement(
                look.x * 5,
                0.2,
                look.z * 5
        );

        player.hurtMarked = true;

        // 500 ms = 10 server ticks.
        explosionTimers.put(player.getUUID(), 10);
    }

    @Override
    public void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Integer timer = explosionTimers.get(uuid);

        if (timer == null) {
            return;
        }

        if (!player.isAlive() || player.isRemoved()) {
            explosionTimers.remove(uuid);
            return;
        }

        ServerLevel level = player.level();

        Vec3 direction = player.getDeltaMovement().normalize();
        Vec3 trail = player.position().subtract(direction.scale(1.2));

        if (timer > 1) {
            explosionTimers.put(uuid, timer - 1);
            return;
        }

        explosionTimers.remove(uuid);
        explode(player);
    }

    private void explode(ServerPlayer player) {
        ServerLevel level = player.level();
        Vec3 pos = player.position();

        level.explode(
                player,
                pos.x,
                pos.y,
                pos.z,
                4.0F,
                false,
                Level.ExplosionInteraction.NONE
        );

        level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.x,
                pos.y + 1,
                pos.z,
                200,
                2.0,
                1.0,
                2.0,
                0.08
        );

        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                pos.x,
                pos.y + 1,
                pos.z,
                10,
                1.33,
                0.66,
                1.33,
                0.05
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.PLAYERS,
                1.33F,
                0.8F
        );
    }
}