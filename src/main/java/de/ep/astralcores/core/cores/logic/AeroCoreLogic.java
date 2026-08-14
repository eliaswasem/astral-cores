package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class AeroCoreLogic {

    // Players that currently have the Aero Core passive active.
    public static final Set<ServerPlayer> activePlayers =
            Collections.newSetFromMap(new WeakHashMap<>());

    // Active Tornado Lift timers.
    private static final Map<ServerPlayer, TickTimer> tornadoTimers =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void applyPassive(ServerPlayer player) {
        activePlayers.add(player);
    }

    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
        tornadoTimers.remove(player);
    }

    public static boolean handleFallShockwave(
            ServerPlayer player,
            DamageSource source
    ) {
        // Ignore the event if the player does not have Aero Core.
        if (!activePlayers.contains(player)) {
            return true;
        }

        // Only react to fall damage.
        if (!source.is(DamageTypes.FALL)) {
            return true;
        }

        float fallDistance = (float) player.fallDistance;
        float rawDamage = fallDistance - 3.0F;

        if (rawDamage > 0) {

            // Maximum shockwave damage is 12 damage.
            float shockwaveDamage =
                    Math.min(rawDamage, 12.0F);

            double maxRadius = 3.5;

            AABB boundingBox =
                    player.getBoundingBox().inflate(maxRadius);

            List<LivingEntity> targets =
                    player.level().getEntitiesOfClass(
                            LivingEntity.class,
                            boundingBox,
                            entity -> entity != player
                    );

            if (player.level() instanceof ServerLevel serverLevel) {

                PlayerData data =
                        AstralCores.PLAYER_DATA.get(player);

                for (LivingEntity target : targets) {

                    // Trusted players are ignored.
                    if (target instanceof ServerPlayer targetPlayer
                            && data != null
                            && data.isTrusted(targetPlayer.getUUID())) {
                        continue;
                    }

                    target.hurtServer(
                            serverLevel,
                            serverLevel.damageSources().magic(),
                            shockwaveDamage
                    );

                    double xDiff =
                            target.getX() - player.getX();

                    double zDiff =
                            target.getZ() - player.getZ();

                    target.knockback(
                            0.08,
                            xDiff,
                            zDiff,
                            serverLevel.damageSources().magic(),
                            shockwaveDamage,
                            false
                    );
                }

                // Expanding cloud shockwave.
                for (
                        double currentRadius = 0.5;
                        currentRadius <= maxRadius;
                        currentRadius += 0.6
                ) {

                    int points =
                            (int) (currentRadius * 8);

                    for (int i = 0; i < points; i++) {

                        double angle =
                                (2 * Math.PI * i) / points;

                        double offsetX =
                                Math.cos(angle) * currentRadius;

                        double offsetZ =
                                Math.sin(angle) * currentRadius;

                        serverLevel.sendParticles(
                                ParticleTypes.CLOUD,
                                player.getX() + offsetX,
                                player.getY() + 0.1,
                                player.getZ() + offsetZ,
                                1,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                        );
                    }
                }

                serverLevel.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.WIND_CHARGE_BURST,
                        SoundSource.PLAYERS,
                        1.2F,
                        1.0F
                );
            }
        }

        // Cancel the original fall damage.
        return false;
    }

    public static void activate(ServerPlayer player) {

        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel world = (ServerLevel) player.level();
        Vec3 center = player.position();

        // Play the activation sound.
        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.WIND_CHARGE_BURST,
                SoundSource.PLAYERS,
                2.0F,
                1.0F
        );

        // Find entities around the player.
        AABB area = new AABB(
                center.x - 5,
                center.y - 2,
                center.z - 5,
                center.x + 5,
                center.y + 5,
                center.z + 5
        );

        List<LivingEntity> targets =
                world.getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        entity -> entity != player
                );

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        for (LivingEntity entity : targets) {

            // Trusted entities are ignored.
            if (data != null
                    && data.isTrusted(entity.getUUID())) {
                continue;
            }

            // Launch the entity upward.
            Effects.applyEffect(
                    entity,
                    MobEffects.LEVITATION,
                    10,
                    45,
                    false,
                    false,
                    false
            );
        }

        // The visual tornado lasts 25 ticks.
        tornadoTimers.put(
                player,
                new TickTimer(25)
        );
    }

    public static void tick(ServerPlayer player) {

        TickTimer timer =
                tornadoTimers.get(player);

        // No active Tornado Lift.
        if (timer == null) {
            return;
        }

        // Cancel the visual if the player is no longer valid.
        if (!player.isAlive() || player.isRemoved()) {
            tornadoTimers.remove(player);
            return;
        }

        spawnTornadoParticles(player, timer);

        // Remove the timer when it expires.
        if (timer.tick()) {
            tornadoTimers.remove(player);
        }
    }

    private static void spawnTornadoParticles(
            ServerPlayer player,
            TickTimer timer
    ) {
        ServerLevel world = player.level();
        Vec3 center = player.position();

        /*
         * Timer starts at 25.
         *
         * 25 remaining -> layer 0
         * 24 remaining -> layer 1
         * ...
         * 1 remaining  -> layer 24
         *
         * This makes the tornado visually rise from
         * the ground to roughly 25 blocks high.
         */
        double yOffset =
                25 - timer.getRemaining();

        for (int degree = 0; degree < 360; degree += 8) {

            double radians =
                    Math.toRadians(degree);

            double x =
                    5.0 * Math.cos(radians);

            double z =
                    5.0 * Math.sin(radians);

            world.sendParticles(
                    ParticleTypes.CLOUD,
                    true,
                    true,
                    center.x + x,
                    center.y + yOffset,
                    center.z + z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}