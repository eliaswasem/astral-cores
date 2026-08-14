package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class GaleCoreLogic {

    // Thread-safe weak mapping to automatically drop entries when a player disconnects.
    private static final Map<ServerPlayer, TickTimer> explosionTimers =
            Collections.synchronizedMap(new WeakHashMap<>());

    private GaleCoreLogic() {
    }

    public static void applyPassive(ServerPlayer player) {
        if (player.isSprinting()) {
            Effects.applyEffect(
                    player,
                    MobEffects.SPEED,
                    25,
                    1
            );
        }
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

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
                player,
                new TickTimer(10)
        );
    }

    public static void tick(ServerPlayer player) {
        // Query the active dash timer directly via the ServerPlayer instance.
        TickTimer timer = explosionTimers.get(player);

        // The player has no active Sonic Dash.
        if (timer == null) {
            return;
        }

        // Cancels the ability if the player is no longer valid.
        if (!player.isAlive() || player.isRemoved()) {
            explosionTimers.remove(player);
            return;
        }

        // Explode when the timer reaches zero.
        if (timer.tick()) {
            explosionTimers.remove(player);
            explode(player);
        }
    }

    public static void onRemoved(ServerPlayer player) {
        // Cancel any Sonic Dash that is currently waiting for its explosion.
        explosionTimers.remove(player);
    }

    private static void explode(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
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
