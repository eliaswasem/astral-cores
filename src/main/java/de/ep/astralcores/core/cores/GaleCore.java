package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
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

    // Stores the explosion timer for each player currently using Sonic Dash.
    private static final Map<UUID, TickTimer> explosionTimers = new HashMap<>();

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

        // Launches the player forward in the direction they are looking.
        player.setDeltaMovement(
                look.x * 5,
                0.2,
                look.z * 5
        );

        player.hurtMarked = true;

        // Starts a 10-tick timer before the Sonic Dash explosion.
        explosionTimers.put(
                player.getUUID(),
                new TickTimer(10)
        );
    }

    @Override
    public void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        TickTimer timer = explosionTimers.get(uuid);

        // The player has no active Sonic Dash.
        if (timer == null) {
            return;
        }

        // Cancels the ability if the player is no longer valid.
        if (!player.isAlive() || player.isRemoved()) {
            explosionTimers.remove(uuid);
            return;
        }

        // TickTimer returns true when the timer reaches zero.
        if (timer.tick()) {
            explosionTimers.remove(uuid);
            explode(player);
        }
    }

    private void explode(ServerPlayer player) {
        ServerLevel level = player.level();
        Vec3 pos = player.position();

        // Creates the visual/audio explosion without block damage.
        level.explode(
                player,
                pos.x,
                pos.y,
                pos.z,
                4.0F,
                false,
                Level.ExplosionInteraction.NONE
        );

        // Large flame burst around the player.
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

        // Sonic Boom particles at the explosion position.
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

        // Plays the Warden charge sound when the dash ends.
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