package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LeviathanCoreLogic {

    public static final Set<ServerPlayer> activePlayers =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final Map<ServerPlayer, TickTimer> trackedPlayers = new ConcurrentHashMap<>();
    private static final Map<ServerPlayer, ServerPlayer> playerTargets = new ConcurrentHashMap<>();


    private static final double PULL_SPEED = 0.65;
    private static final int MAX_PULL_DURATION_TICKS = 40;

    private LeviathanCoreLogic() {
    }

    // Registers the specific player reference inside the active core passive cache.
    public static void applyPassive(ServerPlayer player) {
        activePlayers.add(player);

        if (player.isInWater()) {
            Effects.applyEffect(player, MobEffects.DOLPHINS_GRACE, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.WATER_BREATHING, 20, 2, false, false, false);
            Effects.applyEffect(player, MobEffects.CONDUIT_POWER, 20, 2, false, false, false);
        }
        if (player.isInWaterOrRain()) {
            Effects.applyEffect(player, MobEffects.STRENGTH, 20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.SPEED, 20, 1, false, false, false);
            Effects.applyEffect(player, MobEffects.REGENERATION, 20, 1, false, false, false);
        }
    }

    // Safely removes player profiles and stops pulling targets.
    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
        clearTargetsForPlayer(player);
    }

    // Gathers other players in proximity and starts pulling them.
    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();

        double PULL_RADIUS = player.isInWaterOrRain() ? 6.0 : 4.0;

        AABB searchBox = player.getBoundingBox().inflate(PULL_RADIUS);

        List<ServerPlayer> targetPlayers = level.getEntitiesOfClass(
                ServerPlayer.class,
                searchBox,
                entity -> entity.isAlive() && !entity.equals(player)
        );

        for (ServerPlayer target : targetPlayers) {
            trackedPlayers.put(target, new TickTimer(MAX_PULL_DURATION_TICKS));
            playerTargets.put(target, player);
        }

        // Circular Leviathan collision / suction effect
        for (int i = 0; i < 40; i++) {

        Vec3 pos = player.position();


            // Water particles
            level.sendParticles(
                    ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    PULL_RADIUS -1,
                    0.25,
                    PULL_RADIUS -1,
                    0.05
            );

            // Underwater particles
            level.sendParticles(
                    ParticleTypes.UNDERWATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    PULL_RADIUS -1,
                    0.25,
                    PULL_RADIUS -1,
                    0.05
            );

            // Falling water
            level.sendParticles(
                    ParticleTypes.FALLING_WATER,
                    pos.x,
                    pos.y,
                    pos.z,
                    100,
                    PULL_RADIUS -1,
                    0.25,
                    PULL_RADIUS- 1,
                    0.05
            );
        }
    }




    // Computes relative physical movement vectors for pulled players over server frames.
    public static void tick(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved() || trackedPlayers.isEmpty()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
        Iterator<Map.Entry<ServerPlayer, ServerPlayer>> iterator = playerTargets.entrySet().iterator();

        boolean playerIsPulling = false;
        int lowestTicksLeft = MAX_PULL_DURATION_TICKS;

        while (iterator.hasNext()) {
            Map.Entry<ServerPlayer, ServerPlayer> entry = iterator.next();
            ServerPlayer casterPlayer = entry.getValue();

            if (!casterPlayer.equals(player)) {
                continue;
            }

            ServerPlayer target = entry.getKey();
            TickTimer timer = trackedPlayers.get(target);

            // Purges broken or nonexistent entity listings instantly from tracking arrays.
            if (target == null || !target.isAlive() || target.isRemoved() || timer == null) {
                trackedPlayers.remove(target);
                iterator.remove();
                continue;
            }

            Vec3 playerPos = player.position().add(0, 0.5, 0);
            Vec3 targetPos = target.position();
            double distance = playerPos.distanceTo(targetPos);

            // Releases targets if limits or close proximity are reached.
            if (distance < 1.8 || timer.tick()) {
                trackedPlayers.remove(target);
                iterator.remove();
                continue;
            }

            // Sets customized acceleration values directing targets directly to the caster.
            Vec3 direction = playerPos.subtract(targetPos).normalize();
            Vec3 motion = direction.scale(PULL_SPEED);

            target.setDeltaMovement(motion);
            target.hurtMarked = true;

            target.connection.send(new ClientboundSetEntityMotionPacket(target));

            playerIsPulling = true;
            if (timer.getRemaining() < lowestTicksLeft) {
                lowestTicksLeft = timer.getRemaining();
            }

            // Spawns tiny static electrical charge indicators right above the targets.
            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    targetPos.x, targetPos.y + 1.0, targetPos.z,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }

        double PULL_RADIUS = 4.0f;
        if (player.isInWaterOrRain()) {
            PULL_RADIUS = 6.0f;
        }

        if (playerIsPulling) {
            Vec3 center = player.position();

            double fieldRadius = PULL_RADIUS * ((double) lowestTicksLeft / MAX_PULL_DURATION_TICKS);

            if (fieldRadius > 1.0) {
                for (int i = 0; i < 24; i++) {
                    double angle = (Math.PI * 2 * i) / 24;
                    double x = center.x + Math.cos(angle) * fieldRadius;
                    double z = center.z + Math.sin(angle) * fieldRadius;

                    // Outlines a collapsing gravitational suction border ring along the ground.
                    level.sendParticles(
                            ParticleTypes.PORTAL,
                            x, center.y + 0.15, z,
                            1, 0.0, 0.0, 0.0, 0.0
                    );
                }
            }
        }
    }

    // Restores default environment rules when player network tunnels drop out.
    private static void clearTargetsForPlayer(ServerPlayer player) {
        Iterator<Map.Entry<ServerPlayer, ServerPlayer>> iterator = playerTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ServerPlayer, ServerPlayer> entry = iterator.next();
            if (entry.getValue().equals(player)) {
                ServerPlayer target = entry.getKey();
                trackedPlayers.remove(target);
                iterator.remove();
            }
        }
    }
}
