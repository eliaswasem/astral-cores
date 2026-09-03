package de.ep.astralcores.core.cores.logic;

import com.mojang.datafixers.util.Pair;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.manager.CoreCooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;

import java.util.*;

public class ChronoCoreLogic {


    // Stores up to 10 positions for each player.
    private static final int MAX_POSITION_HISTORY = 10;

    // Keeps each player's position history separate.
    private static final Map<UUID, Deque<PositionSnapshot>> positionHistory = new HashMap<>();

    // Stores a player's position and rotation.
    private record PositionSnapshot(
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) {}

    // applyPassive runs once per second and is used as the position history loop.
    public static void applyPassive(ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Get or create this player's history.
        Deque<PositionSnapshot> history =
                positionHistory.computeIfAbsent(uuid, ignored -> new ArrayDeque<>());

        // Save the player's current position.
        history.addLast(new PositionSnapshot(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                player.getXRot()
        ));

        // Remove the oldest position when the history is full.
        if (history.size() > MAX_POSITION_HISTORY) {
            history.removeFirst();
        }
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {

        // Remove the player's stored position history.
        positionHistory.remove(player.getUUID());
    }

    public static CoreActivationResult activate(ServerPlayer player) {

        // Check if the Chrono Core is active.
        if (!(AstralCores.PLAYER_DATA.get(player).getEquippedCore() == CoreType.CHRONO_CORE)) {
            return CoreActivationResult.FAILED;
        }

        PositionSnapshot target = getReturnPosition(player);

        // Check if enough position history exists.
        if (target == null) {
            player.sendSystemMessage(
                    Component.literal("[Chrono Core] Not enough time history!")
                            .withStyle(ChatFormatting.RED)
            );
            return CoreActivationResult.FAILED;
        }

        // Teleport the player to their position from 5 seconds ago.
        player.teleportTo(
                player.level(),
                target.x(),
                target.y(),
                target.z(),
                Set.of(),
                target.yaw(),
                target.pitch(),
                true
        );

        // Tell the player that Time Return was activated.
        player.sendSystemMessage(
                Component.literal("[Chrono Core] Time Return activated!")
                        .withStyle(ChatFormatting.AQUA)
        );

        return CoreActivationResult.EXECUTED;
    }

    private static PositionSnapshot getReturnPosition(ServerPlayer player) {
        Deque<PositionSnapshot> history = positionHistory.get(player.getUUID());

        // Return nothing if the player has less than 5 seconds of history.
        if (history == null || history.size() < 6) {
            return null;
        }

        PositionSnapshot[] snapshots = history.toArray(new PositionSnapshot[0]);

        // Return the snapshot from approximately 5 seconds ago.
        return snapshots[snapshots.length - 6];
    }

    // Evaluates if the chrono core is equipped and rolls a 50% chance to prevent death.
    public static boolean handleSecondTimeline(ServerPlayer player, DamageSource damageSource, float damageAmount) {

        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        // Stops execution immediately if the player does not have the ChronoCore active in the map.
        if (!(data.getEquippedCore() == CoreType.CHRONO_CORE)) {
            return true;
        }

        if (!CoreCooldownManager.isPassiveReady(data, CoreType.CHRONO_CORE)) {
            player.sendSystemMessage(Component.literal("Second timeline is on Cooldown.")
                    .withStyle(ChatFormatting.RED));
            return true;
        }

        Optional<Core> coreOptional = CoreRegistry.get(CoreType.CHRONO_CORE);

        if (coreOptional.isEmpty()) {
            return true;
        }

        Core core = coreOptional.get();

        CoreCooldownManager.startPassiveCooldown(data, CoreType.CHRONO_CORE, core.getPassiveCooldown());

            // Restores the player to maximum health and resets their combat state.
            player.setHealth(player.getMaxHealth());
            player.getCombatTracker().recheckStatus();

            // Triggers visual and audio totem activation effects.
            playDeathCheatEffects(player);

            player.sendSystemMessage(Component.literal("[Chrono Core] Second Timeline activated! You have been healed.")
                    .withStyle(ChatFormatting.GREEN));

            return false;
    }

    // Handles the particles, sounds, and fake item packets for the death prevention animation.
    private static void playDeathCheatEffects(ServerPlayer player) {
        // Plays the vanilla totem activation sound at the player position.
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F, 1.0F
        );

        // Spawns totem particles around the player location.
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    40, 0.3, 0.5, 0.3, 0.15
            );
        }

        // Fetches the core base item or defaults to a clock item if missing.
        Item registeredItem = CoreRegistry.get(CoreType.CHRONO_CORE)
                .map(Core::getBaseItem)
                .orElse(Items.CLOCK);

        // Builds a temporary item stack configured with death protection attributes.
        ItemStack fakeCoreItem = new ItemStack(registeredItem);
        fakeCoreItem.set(DataComponents.DEATH_PROTECTION, new DeathProtection(List.of()));

        // Sends a fake packet showing the item in the player off-hand slot.
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, fakeCoreItem))
        ));

        // Triggers entity event status 35 to force the client totem screen animation.
        player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));

        // Instantly restores the real server off-hand item data to correct the client HUD display.
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, player.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND)))
        ));
    }

}