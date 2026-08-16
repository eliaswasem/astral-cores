package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LeviathanCoreLogic {

    // Players that currently have the Leviathan Core passive active.
    public static final Set<ServerPlayer> activePlayers = new HashSet<>();

    // Remaining pull duration for each player currently being pulled.
    private static final Map<ServerPlayer, TickTimer> trackedPlayers =
            new HashMap<>();

    // Maps each pulled player to the Leviathan Core user pulling them.
    private static final Map<ServerPlayer, ServerPlayer> playerTargets =
            new HashMap<>();

    private static final double PULL_SPEED = 0.65;
    private static final int MAX_PULL_DURATION_TICKS = 40;

    public static void applyPassive(ServerPlayer player) {
        // Register the player while the core is equipped.
        activePlayers.add(player);

        // Stronger underwater utility effects while fully submerged.
        if (player.isInWater()) {
            Effects.applyEffect(
                    player,
                    MobEffects.DOLPHINS_GRACE,
                    20,
                    2,
                    false,
                    false,
                    false
            );

            Effects.applyEffect(
                    player,
                    MobEffects.WATER_BREATHING,
                    20,
                    2,
                    false,
                    false,
                    false
            );

            Effects.applyEffect(
                    player,
                    MobEffects.CONDUIT_POWER,
                    20,
                    2,
                    false,
                    false,
                    false
            );
        }

        // Combat and regeneration bonuses while in water or rain.
        if (player.isInWaterOrRain()) {
            Effects.applyEffect(
                    player,
                    MobEffects.STRENGTH,
                    20,
                    1,
                    false,
                    false,
                    false
            );

            Effects.applyEffect(
                    player,
                    MobEffects.SPEED,
                    20,
                    1,
                    false,
                    false,
                    false
            );

            Effects.applyEffect(
                    player,
                    MobEffects.REGENERATION,
                    20,
                    1,
                    false,
                    false,
                    false
            );
        }
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        // Remove the player from the active passive cache.
        activePlayers.remove(player);

        // Cancel every pull controlled by this player.
        clearTargetsForPlayer(player);

        // Stop the player from being pulled by another Leviathan.
        trackedPlayers.remove(player);
        playerTargets.remove(player);
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();

        // The ability has a larger pull radius while the caster is in water or rain.
        double pullRadius =
                player.isInWaterOrRain() ? 6.0 : 4.0;

        AABB searchBox =
                player.getBoundingBox().inflate(pullRadius);

        List<ServerPlayer> targetPlayers =
                level.getEntitiesOfClass(
                        ServerPlayer.class,
                        searchBox,
                        entity ->
                                entity.isAlive()
                                        && !entity.equals(player)
                );

        // PlayerData is fetched once for this activation instead of once per target.
        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        for (ServerPlayer target : targetPlayers) {

            // Trusted players are completely ignored by the pull.
            if (data != null && data.isTrusted(target.getUUID())) {
                continue;
            }

            // Start a fresh 2-second pull on the target.
            trackedPlayers.put(
                    target,
                    new TickTimer(MAX_PULL_DURATION_TICKS)
            );

            // Remember which Leviathan user owns this pull.
            playerTargets.put(target, player);
        }

        Vec3 pos = player.position();

        // Creates the large water particle burst around the caster.
        for (int i = 0; i < 40; i++) {

            level.sendParticles(
                    ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    pullRadius - 1,
                    0.25,
                    pullRadius - 1,
                    0.05
            );

            level.sendParticles(
                    ParticleTypes.UNDERWATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    pullRadius - 1,
                    0.25,
                    pullRadius - 1,
                    0.05
            );

            level.sendParticles(
                    ParticleTypes.FALLING_WATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    pullRadius - 1,
                    0.25,
                    pullRadius - 1,
                    0.05
            );
        }
    }

    public static void tick(ServerPlayer player) {
        if (!player.isAlive()
                || player.isRemoved()
                || trackedPlayers.isEmpty()) {
            return;
        }

        ServerLevel level =
                (ServerLevel) player.level();

        // Fetch PlayerData once per tick and reuse it for every target.
        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        Iterator<Map.Entry<ServerPlayer, ServerPlayer>> iterator =
                playerTargets.entrySet().iterator();

        boolean playerIsPulling = false;
        int lowestTicksLeft = MAX_PULL_DURATION_TICKS;

        while (iterator.hasNext()) {

            Map.Entry<ServerPlayer, ServerPlayer> entry =
                    iterator.next();

            ServerPlayer casterPlayer =
                    entry.getValue();

            // This target belongs to another Leviathan user.
            if (!casterPlayer.equals(player)) {
                continue;
            }

            ServerPlayer target =
                    entry.getKey();

            TickTimer timer =
                    trackedPlayers.get(target);

            // Remove invalid or already-expired pull entries.
            if (target == null
                    || !target.isAlive()
                    || target.isRemoved()
                    || timer == null) {

                trackedPlayers.remove(target);
                iterator.remove();
                continue;
            }

            // Trusted players are ignored even if they became trusted
            // after the ability was activated.
            if (data != null && data.isTrusted(target.getUUID())) {
                trackedPlayers.remove(target);
                iterator.remove();
                continue;
            }

            Vec3 playerPos =
                    player.position().add(0, 0.5, 0);

            Vec3 targetPos =
                    target.position();

            double distance =
                    playerPos.distanceTo(targetPos);

            // Stop pulling once the target is close enough or the timer expires.
            if (distance < 1.8 || timer.tick()) {
                trackedPlayers.remove(target);
                iterator.remove();
                continue;
            }

            // Calculate the direction from the target toward the caster.
            Vec3 direction =
                    playerPos
                            .subtract(targetPos)
                            .normalize();

            // Apply constant movement toward the Leviathan user.
            Vec3 motion =
                    direction.scale(PULL_SPEED);

            target.setDeltaMovement(motion);
            target.hurtMarked = true;

            playerIsPulling = true;

            // Used to calculate the shrinking visual pull radius.
            if (timer.getRemaining() < lowestTicksLeft) {
                lowestTicksLeft =
                        timer.getRemaining();
            }

            // Small electrical particles show which entities are currently being pulled.
            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    targetPos.x,
                    targetPos.y + 1.0,
                    targetPos.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }

        // The pull radius depends on the caster's current environment.
        double pullRadius = 4.0;

        if (player.isInWaterOrRain()) {
            pullRadius = 6.0;
        }

        if (playerIsPulling) {

            Vec3 center =
                    player.position();

            // The visual field contracts as the pull approaches its end.
            double fieldRadius =
                    pullRadius
                            * ((double) lowestTicksLeft
                            / MAX_PULL_DURATION_TICKS);

            if (fieldRadius > 1.0) {

                // Draw the shrinking portal ring around the caster.
                for (int i = 0; i < 24; i++) {

                    double angle =
                            (Math.PI * 2 * i) / 24;

                    double x =
                            center.x
                                    + Math.cos(angle) * fieldRadius;

                    double z =
                            center.z
                                    + Math.sin(angle) * fieldRadius;

                    level.sendParticles(
                            ParticleTypes.PORTAL,
                            x,
                            center.y + 0.15,
                            z,
                            1,
                            0.0,
                            0.0,
                            0.0,
                            0.0
                    );
                }
            }
        }
    }

    private static void clearTargetsForPlayer(ServerPlayer player) {

        // Remove every target whose pull is controlled by this player.
        Iterator<Map.Entry<ServerPlayer, ServerPlayer>> iterator =
                playerTargets.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<ServerPlayer, ServerPlayer> entry =
                    iterator.next();

            if (entry.getValue().equals(player)) {

                ServerPlayer target =
                        entry.getKey();

                trackedPlayers.remove(target);
                iterator.remove();
            }
        }
    }
}