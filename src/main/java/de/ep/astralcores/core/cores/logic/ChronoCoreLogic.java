package de.ep.astralcores.core.cores.logic;

import com.mojang.datafixers.util.Pair;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChronoCoreLogic {

    // Tracks which players currently have this core's passive effect active
    public static final Set<UUID> activePlayers = new HashSet<>();

    public static void applyPassive(ServerPlayer player) {
        // Marks this player as having the core active
        activePlayers.add(player.getUUID());
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        activePlayers.remove(player.getUUID());
    }

    // Evaluates if the chrono core is equipped and rolls a 50% chance to prevent death
    public static boolean handleSecondTimeline(ServerPlayer player, DamageSource damageSource, float damageAmount) {
        // Stops execution immediately if the player does not have the ChronoCore active in the map
        if (!activePlayers.contains(player.getUUID())) {
            return true;
        }

        // Rolls a 50% success chance to trigger the death cheat mechanic
        if (player.getRandom().nextBoolean()) {

            // Restores the player to maximum health and resets their combat state
            player.setHealth(player.getMaxHealth());
            player.getCombatTracker().recheckStatus();

            // Triggers visual and audio totem activation effects
            playDeathCheatEffects(player);

            player.sendSystemMessage(Component.literal("[Chrono Core] Second Timeline activated! You have been healed.")
                    .withStyle(ChatFormatting.GREEN));

            return false;
        } else {

            player.sendSystemMessage(Component.literal("[Chrono Core] Second Timeline failed!")
                    .withStyle(ChatFormatting.RED));

            return true;
        }
    }

    // Handles the particles, sounds, and fake item packets for the death prevention animation
    private static void playDeathCheatEffects(ServerPlayer player) {
        // Plays the vanilla totem activation sound at the player position
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F, 1.0F
        );

        // Spawns totem particles around the player location
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    40, 0.3, 0.5, 0.3, 0.15
            );
        }

        // Fetches the core base item or defaults to a clock item if missing
        Item registeredItem = CoreRegistry.get(CoreType.CHRONO_CORE)
                .map(Core::getBaseItem)
                .orElse(Items.CLOCK);

        // Builds a temporary item stack configured with death protection attributes
        ItemStack fakeCoreItem = new ItemStack(registeredItem);
        fakeCoreItem.set(DataComponents.DEATH_PROTECTION, new DeathProtection(List.of()));

        // Sends a fake packet showing the item in the player off-hand slot
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, fakeCoreItem))
        ));

        // Triggers entity event status 35 to force the client totem screen animation
        player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));

        // Instantly restores the real server off-hand item data to correct the client HUD display
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, player.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND)))
        ));
    }

}