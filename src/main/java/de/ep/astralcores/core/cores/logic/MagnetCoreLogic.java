package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MagnetCoreLogic {

    // Stores the remaining pull duration for each tracked entity.
    private static final Map<Entity, TickTimer> trackedItems =
            new HashMap<>();

    // Associates each tracked entity with the player currently pulling it.
    private static final Map<Entity, UUID> itemCasters =
            new HashMap<>();

    private static final double PULL_RADIUS = 15.0D;
    private static final double PULL_SPEED = 0.55D;
    private static final int MAX_PULL_DURATION_TICKS = 40;

    public static void applyPassive(ServerPlayer player) {
        // Handles passive effects while the core is equipped.
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        clearItemsForPlayer(player.getUUID());
    }

    public static CoreActivationResult activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return CoreActivationResult.FAILED;
        }

        ServerLevel level = player.level();

        AABB searchBox =
                player.getBoundingBox().inflate(PULL_RADIUS);

        List<Entity> targetItems =
                level.getEntitiesOfClass(
                        Entity.class,
                        searchBox,
                        entity ->
                                entity.isAlive()
                                        && (entity instanceof ItemEntity
                                        || entity instanceof ExperienceOrb)
                );

        UUID casterUUID =
                player.getUUID();

        for (Entity item : targetItems) {

            if (item instanceof ItemEntity itemEntity) {
                itemEntity.noPhysics = true;
            }

            trackedItems.put(
                    item,
                    new TickTimer(MAX_PULL_DURATION_TICKS)
            );

            itemCasters.put(
                    item,
                    casterUUID
            );
        }

        Vec3 origin =
                player.position();

        for (int i = 0; i < 40; i++) {

            double angle =
                    Math.PI * 2.0D * i / 40.0D;

            double x =
                    origin.x
                            + Math.cos(angle) * PULL_RADIUS;

            double z =
                    origin.z
                            + Math.sin(angle) * PULL_RADIUS;

            level.sendParticles(
                    ParticleTypes.CLOUD,
                    x,
                    origin.y + 0.15D,
                    z,
                    1,
                    0.0D,
                    0.01D,
                    0.0D,
                    0.005D
            );
        }

        return CoreActivationResult.EXECUTED;
    }

    public static void tick(ServerPlayer player) {
        if (!player.isAlive()
                || player.isRemoved()
                || trackedItems.isEmpty()) {
            return;
        }

        ServerLevel level =
                player.level();

        UUID playerUUID =
                player.getUUID();

        Iterator<Map.Entry<Entity, UUID>> iterator =
                itemCasters.entrySet().iterator();

        boolean playerIsPulling = false;

        int lowestTicksLeft =
                MAX_PULL_DURATION_TICKS;

        while (iterator.hasNext()) {

            Map.Entry<Entity, UUID> entry =
                    iterator.next();

            Entity item =
                    entry.getKey();

            UUID casterUUID =
                    entry.getValue();

            if (!casterUUID.equals(playerUUID)) {
                continue;
            }

            TickTimer timer =
                    trackedItems.get(item);

            if (item == null
                    || !item.isAlive()
                    || timer == null) {

                resetItem(item);

                trackedItems.remove(item);
                iterator.remove();

                continue;
            }

            Vec3 playerPos =
                    player.position()
                            .add(0.0D, 0.5D, 0.0D);

            Vec3 itemPos =
                    item.position();

            double distance =
                    playerPos.distanceTo(itemPos);

            if (distance < 1.2D
                    || timer.tick()) {

                resetItem(item);

                trackedItems.remove(item);
                iterator.remove();

                continue;
            }

            if (item instanceof ItemEntity itemEntity) {
                itemEntity.noPhysics = true;
            }

            Vec3 direction =
                    playerPos
                            .subtract(itemPos)
                            .normalize();

            item.setDeltaMovement(
                    direction.scale(PULL_SPEED)
            );

            playerIsPulling = true;

            lowestTicksLeft =
                    Math.min(
                            lowestTicksLeft,
                            timer.getRemaining()
                    );

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    itemPos.x,
                    itemPos.y + 0.2D,
                    itemPos.z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        if (!playerIsPulling) {
            return;
        }

        Vec3 center =
                player.position();

        double fieldRadius =
                PULL_RADIUS
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
                            + Math.cos(angle) * fieldRadius;

            double z =
                    center.z
                            + Math.sin(angle) * fieldRadius;

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

    public static void executeMagneticDisarm(
            ServerPlayer attacker,
            ServerPlayer victim
    ) {
        PlayerData data =
                AstralCores.PLAYER_DATA.get(attacker);

        if (data == null) {
            return;
        }

        if (!(data.getEquippedCore() == CoreType.MAGNET_CORE)) {
            return;
        }
        if (data.isTrusted(victim.getUUID())) {
            return;
        }

        if (attacker.getRandom().nextFloat() >= 0.50F) {
            return;
        }

        Effects.applyEffect(
                victim,
                MobEffects.MINING_FATIGUE,
                30,
                2,
                false,
                false,
                false
        );

        victim.sendSystemMessage(
                Component.literal("Your Weapon is Magnetized")
                        .withStyle(
                                ChatFormatting.BOLD,
                                ChatFormatting.WHITE
                        )
        );
    }

    private static void clearItemsForPlayer(UUID playerUUID) {

        Iterator<Map.Entry<Entity, UUID>> iterator =
                itemCasters.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<Entity, UUID> entry =
                    iterator.next();

            if (!entry.getValue().equals(playerUUID)) {
                continue;
            }

            Entity item =
                    entry.getKey();

            resetItem(item);

            trackedItems.remove(item);
            iterator.remove();
        }
    }

    private static void resetItem(Entity item) {
        if (item instanceof ItemEntity itemEntity) {
            itemEntity.noPhysics = false;
        }
    }
}