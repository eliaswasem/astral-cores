package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagnetCore extends Core {

    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    // Eine Map, die speichert, welche Items wie viele Ticks lang zum Spieler gezogen werden sollen
    // Key: Das Item, Value: Die verbleibenden Ticks (z.B. 40 Ticks = 2 Sekunden Sog)
    private static final Map<ItemEntity, Integer> trackedItems = new ConcurrentHashMap<>();
    private static final Map<ItemEntity, ServerPlayer> itemTargets = new ConcurrentHashMap<>();

    private static final double PULL_RADIUS = 15.0;
    private static final double PULL_SPEED = 0.55; // Leicht erhöht für kraftvolleren Zug
    private static final int MAX_PULL_DURATION_TICKS = 40; // Maximal 2 Sekunden aktiver Sog pro Item

    public MagnetCore() {
        super(
                CoreType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        "§4[Active: Magnetic Pull]"
                ),
                10012,
                0,
                0,
                "Magnetic Pull",
                "Magnetic Disarm",
                "\uE00C"
        );


        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<ItemEntity, Integer>> iterator = trackedItems.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemEntity, Integer> entry = iterator.next();
                ItemEntity item = entry.getKey();
                int ticksLeft = entry.getValue();
                ServerPlayer targetPlayer = itemTargets.get(item);

                if (item == null || !item.isAlive() || targetPlayer == null || !targetPlayer.isAlive()) {
                    iterator.remove();
                    itemTargets.remove(item);
                    continue;
                }

                if (ticksLeft <= 0) {
                    item.noPhysics = false;
                    iterator.remove();
                    itemTargets.remove(item);
                    continue;
                }

                Vec3 playerPos = targetPlayer.position().add(0, 0.5, 0);
                Vec3 itemPos = item.position();
                double distance = playerPos.distanceTo(itemPos);

                if (distance < 1.2) {
                    item.noPhysics = false;
                    iterator.remove();
                    itemTargets.remove(item);
                    continue;
                }

                item.noPhysics = true;

                Vec3 direction = playerPos.subtract(itemPos).normalize();
                item.setDeltaMovement(direction.scale(PULL_SPEED));


                trackedItems.put(item, ticksLeft - 1);
            }
        });
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        activePlayers.add(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        ServerLevel level = player.level();
        AABB searchBox = player.getBoundingBox().inflate(PULL_RADIUS);

        List<ItemEntity> targetItems = level.getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                entity -> true
        );

        for (ItemEntity item : targetItems) {
            item.noPhysics = true;
            trackedItems.put(item, MAX_PULL_DURATION_TICKS);
            itemTargets.put(item, player);
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
    }

    public static void executeMagneticDisarm(ServerPlayer attacker, ServerPlayer victim) {
        if (!MagnetCore.activePlayers.contains(attacker)) {
            return;
        }

        var data = AstralCores.PLAYER_DATA.get(attacker);
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
                255,
                false,
                false,
                false
        );

        victim.sendSystemMessage(
                Component.literal("Your Weapon is Magnetized")
                        .withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)
        );
    }
}
