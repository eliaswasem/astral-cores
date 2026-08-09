package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.cores.AeroCore;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;

public class AeroCorePassiveLogic {

    // Handles the passive wind shockwave when an AeroCore player lands from a fall
    public static boolean handleFallShockwave(ServerPlayer player, DamageSource source) {
        // Stops execution immediately if the player does not have the AeroCore active
        if (!AeroCore.activePlayers.contains(player)) {
            return true;
        }

        // Checks if the incoming damage source is fall damage
        if (source.is(DamageTypes.FALL)) {

            // Calculates hypothetical damage based on standard Minecraft fall math
            float fallDistance = (float) player.fallDistance;
            float rawDamage = (fallDistance - 3.0f);

            if (rawDamage > 0) {
                // Scales dynamically up to a maximum cap of 12.0f damage (6 hearts)
                float shockwaveDamage = Math.min(rawDamage, 12.0f);

                // Defines a 3.5 block radius bounding box around the player
                double maxRadius = 3.5;
                AABB boundingBox = player.getBoundingBox().inflate(maxRadius);

                // Gathers all living entities within the radius, excluding the player themselves
                List<LivingEntity> targets = player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        boundingBox,
                        entity -> entity != player
                );

                // Safely processes damage, knockback, and expanding wave particles on ServerLevel
                if (player.level() instanceof ServerLevel serverLevel) {

                    if (!targets.isEmpty()) {
                        // Fetches the player data of the core owner for trust verification
                        PlayerData data = AstralCores.PLAYER_DATA.get(player);

                        for (LivingEntity target : targets) {

                            // Cancels damage if the target player is trusted by the core owner
                            if (target instanceof ServerPlayer targetPlayer && data != null) {
                                if (data.isTrusted(targetPlayer.getUUID())) {
                                    continue;
                                }
                            }

                            // Applies the scaled damage using the server-specific method
                            target.hurtServer(serverLevel, serverLevel.damageSources().magic(), shockwaveDamage);

                            // Applies a custom knockback effect to push targets away from the center
                            double xDiff = target.getX() - player.getX();
                            double zDiff = target.getZ() - player.getZ();
                            target.knockback(0.08, xDiff, zDiff, serverLevel.damageSources().magic(), shockwaveDamage, false);
                        }
                    }

                    // Spawns an expanding wave of cloud particles radiating outward
                    for (double currentRadius = 0.5; currentRadius <= maxRadius; currentRadius += 0.6) {
                        int points = (int) (currentRadius * 8);
                        for (int i = 0; i < points; i++) {
                            double angle = (2 * Math.PI * i) / points;
                            double offsetX = Math.cos(angle) * currentRadius;
                            double offsetZ = Math.sin(angle) * currentRadius;

                            serverLevel.sendParticles(
                                    ParticleTypes.CLOUD,
                                    player.getX() + offsetX,
                                    player.getY() + 0.1,
                                    player.getZ() + offsetZ,
                                    1, 0.0, 0.0, 0.0, 0.0
                            );
                        }
                    }

                    // Plays a deep wind burst audio effect at the impact location
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.WIND_CHARGE_BURST, SoundSource.PLAYERS, 1.2f, 1.0f);
                }
            }
            // Cancels the fall damage so the player survives the landing without losing health
            return false;
        }
        return true;
    }
}
