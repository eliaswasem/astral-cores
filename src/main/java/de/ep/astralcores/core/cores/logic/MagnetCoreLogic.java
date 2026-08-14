package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class MagnetCoreLogic {

    // Tracks players actively wearing the core with self-cleaning weak references.
    public static final Set<ServerPlayer> activePlayers =
            Collections.newSetFromMap(new WeakHashMap<>());

    // Thread-safe weak reference tracking structures mapping the core timers per item.
    private static final Map<ItemEntity, TickTimer> trackedItems = new ConcurrentHashMap<>();
    private static final Map<ItemEntity, ServerPlayer> itemTargets = new ConcurrentHashMap<>();

    private static final double PULL_RADIUS = 15.0;
    private static final double PULL_SPEED = 0.55;
    private static final int MAX_PULL_DURATION_TICKS = 40;

    private MagnetCoreLogic() {
    }

    // Registers the specific player reference inside the active core passive cache.
    public static void applyPassive(ServerPlayer player) {
        activePlayers.add(player);
    }

    // Safely removes player profiles and returns all targeted drops back to gravity.
    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
        clearItemsForPlayer(player);
    }

    // Gathers loose item objects in proximity and overrides their collision grids.
    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
        AABB searchBox = player.getBoundingBox().inflate(PULL_RADIUS);

        List<ItemEntity> targetItems = level.getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                entity -> entity.isAlive()
        );

        for (ItemEntity item : targetItems) {
            item.noPhysics = true;
            trackedItems.put(item, new TickTimer(MAX_PULL_DURATION_TICKS));
            itemTargets.put(item, player);
        }

        Vec3 origin = player.position();
        for (int i = 0; i < 40; i++) {
            double angle = (Math.PI * 2 * i) / 40;
            double xOffset = Math.cos(angle) * PULL_RADIUS;
            double zOffset = Math.sin(angle) * PULL_RADIUS;

            // Spawns an instant expanding dust shell line to notify the user of reach.
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    origin.x + xOffset, origin.y + 0.15, origin.z + zOffset,
                    1, 0.0, 0.01, 0.0, 0.005
            );
        }
    }

    // Computes relative physical movement vectors for pulled drops over server frames.
    public static void tick(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved() || trackedItems.isEmpty()) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
        Iterator<Map.Entry<ItemEntity, ServerPlayer>> iterator = itemTargets.entrySet().iterator();

        boolean playerIsPulling = false;
        int lowestTicksLeft = MAX_PULL_DURATION_TICKS;

        while (iterator.hasNext()) {
            Map.Entry<ItemEntity, ServerPlayer> entry = iterator.next();
            ServerPlayer targetPlayer = entry.getValue();

            if (!targetPlayer.equals(player)) {
                continue;
            }

            ItemEntity item = entry.getKey();
            TickTimer timer = trackedItems.get(item);

            // Purges broken or nonexistent entity listings instantly from tracking arrays.
            if (item == null || !item.isAlive() || timer == null) {
                if (item != null) item.noPhysics = false;
                trackedItems.remove(item);
                iterator.remove();
                continue;
            }

            Vec3 playerPos = player.position().add(0, 0.5, 0);
            Vec3 itemPos = item.position();
            double distance = playerPos.distanceTo(itemPos);

            // Re-enables block clipping and releases targets if limits are reached.
            if (distance < 1.2 || timer.tick()) {
                item.noPhysics = false;
                trackedItems.remove(item);
                iterator.remove();
                continue;
            }

            // Sets customized acceleration values directing items directly to the caster.
            item.noPhysics = true;
            Vec3 direction = playerPos.subtract(itemPos).normalize();
            item.setDeltaMovement(direction.scale(PULL_SPEED));

            playerIsPulling = true;
            if (timer.getRemaining() < lowestTicksLeft) {
                lowestTicksLeft = timer.getRemaining();
            }

            // Spawns tiny static electrical charge indicators right above the drops.
            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    itemPos.x, itemPos.y + 0.2, itemPos.z,
                    1, 0.0, 0.0, 0.0, 0.0
            );
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

    // Handles damage listener sweeps to stall offensive enemy swing capabilities.
    public static void executeMagneticDisarm(ServerPlayer attacker, ServerPlayer victim) {
        if (!activePlayers.contains(attacker)) {
            return;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(attacker);
        if (data != null && data.isTrusted(victim.getUUID())) {
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
                        .withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)
        );
    }

    // Restores default world environment rules when player network tunnels drop out.
    private static void clearItemsForPlayer(ServerPlayer player) {
        Iterator<Map.Entry<ItemEntity, ServerPlayer>> iterator = itemTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ItemEntity, ServerPlayer> entry = iterator.next();
            if (entry.getValue().equals(player)) {
                ItemEntity item = entry.getKey();
                if (item != null && item.isAlive()) {
                    item.noPhysics = false;
                }
                trackedItems.remove(item);
                iterator.remove();
            }
        }
    }
}
