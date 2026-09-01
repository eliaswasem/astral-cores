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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LeviathanCoreLogic {

    private static final Map<UUID, TickTimer> pullTimers = new HashMap<>();
    private static final Map<UUID, UUID> pullCasters = new HashMap<>();

    private static final double PULL_SPEED = 0.65D;
    private static final int MAX_PULL_DURATION_TICKS = 40;

    public static void applyPassive(ServerPlayer player) {
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
        UUID playerUUID = player.getUUID();

        clearPullsByCaster(playerUUID);

        pullTimers.remove(playerUUID);
        pullCasters.remove(playerUUID);
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = player.level();

        boolean inWaterOrRain =
                player.isInWaterOrRain();

        double pullRadius =
                inWaterOrRain ? 6.0D : 4.0D;

        double particleRadius =
                inWaterOrRain ? 4.0D : 3.0D;

        AABB searchBox =
                player.getBoundingBox().inflate(pullRadius);

        List<ServerPlayer> targets =
                level.getEntitiesOfClass(
                        ServerPlayer.class,
                        searchBox,
                        target ->
                                target.isAlive()
                                        && !target.equals(player)
                );

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        UUID casterUUID =
                player.getUUID();

        for (ServerPlayer target : targets) {

            UUID targetUUID =
                    target.getUUID();

            if (data != null
                    && data.isTrusted(targetUUID)) {
                continue;
            }

            pullTimers.put(
                    targetUUID,
                    new TickTimer(MAX_PULL_DURATION_TICKS)
            );

            pullCasters.put(
                    targetUUID,
                    casterUUID
            );
        }

        Vec3 position =
                player.position();

        if (player.isInWater()) {
            return;
        }

        for (int i = 0; i < 40; i++) {

            level.sendParticles(
                    ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                    position.x,
                    position.y,
                    position.z,
                    100,
                    particleRadius,
                    0.25D,
                    particleRadius,
                    0.05D
            );

            level.sendParticles(
                    ParticleTypes.UNDERWATER,
                    position.x,
                    position.y,
                    position.z,
                    100,
                    particleRadius,
                    0.25D,
                    particleRadius,
                    0.05D
            );

            level.sendParticles(
                    ParticleTypes.FALLING_WATER,
                    position.x,
                    position.y,
                    position.z,
                    100,
                    particleRadius,
                    0.25D,
                    particleRadius,
                    0.05D
            );
        }
    }

    public static void tick(ServerPlayer caster) {
        if (!caster.isAlive()
                || caster.isRemoved()
                || pullTimers.isEmpty()) {
            return;
        }

        ServerLevel level =
                caster.level();

        UUID casterUUID =
                caster.getUUID();

        PlayerData data =
                AstralCores.PLAYER_DATA.get(caster);

        Iterator<Map.Entry<UUID, UUID>> iterator =
                pullCasters.entrySet().iterator();

        boolean isPulling = false;
        int lowestTicksLeft =
                MAX_PULL_DURATION_TICKS;

        while (iterator.hasNext()) {

            Map.Entry<UUID, UUID> entry =
                    iterator.next();

            UUID targetUUID =
                    entry.getKey();

            UUID ownerUUID =
                    entry.getValue();

            if (!ownerUUID.equals(casterUUID)) {
                continue;
            }

            TickTimer timer =
                    pullTimers.get(targetUUID);

            ServerPlayer target =
                    AstralCores.getServer()
                            .getPlayerList()
                            .getPlayer(targetUUID);

            if (target == null
                    || !target.isAlive()
                    || target.isRemoved()
                    || timer == null) {

                pullTimers.remove(targetUUID);
                iterator.remove();
                continue;
            }

            if (data != null
                    && data.isTrusted(targetUUID)) {

                pullTimers.remove(targetUUID);
                iterator.remove();
                continue;
            }

            Vec3 casterPosition =
                    caster.position()
                            .add(0.0D, 0.5D, 0.0D);

            Vec3 targetPosition =
                    target.position();

            double distance =
                    casterPosition.distanceTo(targetPosition);

            if (distance < 1.8D
                    || timer.tick()) {

                pullTimers.remove(targetUUID);
                iterator.remove();
                continue;
            }

            Vec3 direction =
                    casterPosition
                            .subtract(targetPosition)
                            .normalize();

            target.setDeltaMovement(
                    direction.scale(PULL_SPEED)
            );

            target.hurtMarked = true;

            isPulling = true;

            lowestTicksLeft =
                    Math.min(
                            lowestTicksLeft,
                            timer.getRemaining()
                    );

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    targetPosition.x,
                    targetPosition.y + 1.0D,
                    targetPosition.z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        if (!isPulling) {
            return;
        }

        double pullRadius =
                caster.isInWaterOrRain()
                        ? 6.0D
                        : 4.0D;

        Vec3 center =
                caster.position();

        double fieldRadius =
                pullRadius
                        * ((double) lowestTicksLeft
                        / MAX_PULL_DURATION_TICKS);

        if (fieldRadius <= 1.0D) {
            return;
        }

        for (int i = 0; i < 24; i++) {

            double angle =
                    Math.PI * 2.0D * i / 24.0D;

            double x =
                    center.x
                            + Math.cos(angle)
                            * fieldRadius;

            double z =
                    center.z
                            + Math.sin(angle)
                            * fieldRadius;

            level.sendParticles(
                    ParticleTypes.PORTAL,
                    x,
                    center.y + 0.15D,
                    z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static void clearPullsByCaster(UUID casterUUID) {

        Iterator<Map.Entry<UUID, UUID>> iterator =
                pullCasters.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UUID, UUID> entry =
                    iterator.next();

            if (entry.getValue().equals(casterUUID)) {

                UUID targetUUID =
                        entry.getKey();

                pullTimers.remove(targetUUID);
                iterator.remove();
            }
        }
    }
}
