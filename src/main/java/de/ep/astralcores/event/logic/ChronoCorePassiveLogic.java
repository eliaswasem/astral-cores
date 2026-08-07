package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;
import com.mojang.datafixers.util.Pair;
import java.util.List;

public class ChronoCorePassiveLogic {

    /**
     * Checks if the player has the Chrono Core equipped and rolls a 50% chance to cheat death.
     * @return false if death is prevented; true if the player should die normally.
     */
    public static boolean handleSecondTimeline(ServerPlayer player, DamageSource damageSource, float damageAmount) {

        /* Validate if player profile exists and Chrono Core is actively equipped */
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null || data.getEquippedCore() != CoreType.CHRONO_CORE) {
            return true;
        }

        /* Roll the 50% success chance */
        if (player.getRandom().nextBoolean()) {

            /* Cancel death lifecycle and restore full health stability */
            player.setHealth(player.getMaxHealth());
            player.getCombatTracker().recheckStatus();

            /* Trigger the vanilla death protection animation utilizing packet-level fake equipment */
            playDeathCheatEffects(player);

            /* Simple text success message */
            player.sendSystemMessage(Component.literal("[Chrono Core] Second Timeline activated! You have been healed.")
                    .withStyle(ChatFormatting.GREEN));

            return false;
        } else {

            /* Simple text failure message */
            player.sendSystemMessage(Component.literal("[Chrono Core] Second Timeline failed!")
                    .withStyle(ChatFormatting.RED));

            return true;
        }
    }

    private static void playDeathCheatEffects(ServerPlayer player) {
        /* Broadcast the standard vanilla Totem activation sound profile at player position */
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F, 1.0F
        );

        /* Spawn vanilla totem particles globally on the server level for all nearby tracking players */
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    40, 0.3, 0.5, 0.3, 0.15
            );
        }

        /**
         * Resolves the backing vanilla item directly through the central core registry instance lookup map.
         * Falls back onto a basic clock item instance if the registration data container misses.
         */
        Item registeredItem = CoreRegistry.get(CoreType.CHRONO_CORE)
                .map(Core::getBaseItem)
                .orElse(Items.CLOCK);

        /* Construct a fake visual item stack dynamically fetching the registered base item from the core type */
        ItemStack fakeCoreItem = new ItemStack(registeredItem);
        fakeCoreItem.set(DataComponents.DEATH_PROTECTION, new DeathProtection(List.of()));

        /**
         * Send a packet telling the client they are holding the core item in their off-hand.
         * This does NOT change the server inventory and does not touch any real item.
         */
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, fakeCoreItem))
        ));

        /* Dispatch entity status packet 35. The client now sees the core item in its off-hand and renders it */
        player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));

        /**
         * Instantly resend the actual server inventory state for the off-hand slot to the client.
         * This cleans up the fake item visualization immediately so the player's HUD stays perfectly accurate.
         */
        player.connection.send(new ClientboundSetEquipmentPacket(
                player.getId(),
                List.of(Pair.of(EquipmentSlot.OFFHAND, player.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND)))
        ));
    }
}
