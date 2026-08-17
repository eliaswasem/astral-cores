package de.ep.astralcores.core.cores.logic;

import com.mojang.datafixers.util.Pair;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class ShadowCoreLogic {

    // Stores the number of seconds each player has been crouching in darkness.
    private static final Map<UUID, Integer> sneakTimers =
            new HashMap<>();

    // Stores players that are currently concealed by the Shadow Core.
    private static final Set<UUID> hiddenPlayers =
            new HashSet<>();

    private static final int MAX_LIGHT_LEVEL = 0;

    // The player must remain crouched in darkness for this many seconds.
    private static final int TIME_THRESHOLD_SECONDS = 5;

    private ShadowCoreLogic() {
    }

    public static void applyPassive(ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Get the block light level at the player's current position.
        int blockLight = player.level().getBrightness(
                LightLayer.BLOCK,
                player.blockPosition()
        );

        // Check whether there is currently skylight at the player's position.
        boolean isOutdoors = player.level().getBrightness(
                LightLayer.SKY,
                player.blockPosition()
        ) > 0;

        // The Shadow Core requires complete darkness to activate.
        boolean isDarkEnough =
                blockLight <= MAX_LIGHT_LEVEL
                        && (!isOutdoors || player.level().isDarkOutside());

        // Maintain the effects while the player is already hidden.
        if (hiddenPlayers.contains(uuid)) {
            // Keep duration above the flicker limit.
            Effects.applyEffect(
                    player,
                    MobEffects.INVISIBILITY,
                    300,
                    1
            );

            Effects.applyEffect(
                    player,
                    MobEffects.NIGHT_VISION,
                    300,
                    1
            );

            // Sprinting or leaving the darkness immediately breaks concealment.
            if (player.isSprinting() || !isDarkEnough) {
                revealPlayer(player);
            }

            return;
        }

        // Start counting while the player remains crouched in darkness.
        if (player.isCrouching() && isDarkEnough) {
            int secondsPassed =
                    sneakTimers.getOrDefault(uuid, 0) + 1;

            sneakTimers.put(uuid, secondsPassed);

            // The player has crouched in darkness for the required duration.
            if (secondsPassed >= TIME_THRESHOLD_SECONDS) {
                hiddenPlayers.add(uuid);
                sneakTimers.remove(uuid);

                Effects.applyEffect(
                        player,
                        MobEffects.INVISIBILITY,
                        300,
                        1
                );

                Effects.applyEffect(
                        player,
                        MobEffects.NIGHT_VISION,
                        300,
                        1
                );

                // Hide the player's armor and held items from tracking players.
                sendFakeEquipmentPackets(player, true);

                player.sendSystemMessage(
                        Component.literal(
                                "[Living Shadow] You dissolved into the shadows."
                        ).withStyle(ChatFormatting.DARK_PURPLE),
                        false
                );

            } else {
                // Calculate the remaining time before concealment.
                int remainingSeconds =
                        TIME_THRESHOLD_SECONDS - secondsPassed;

                // Show the countdown in the action bar.
                player.sendSystemMessage(
                        Component.literal(
                                "[Living Shadow] Dissolving in "
                                        + remainingSeconds
                                        + "s..."
                        ).withStyle(ChatFormatting.GRAY),
                        false
                );
            }

        } else {
            // Cancel the concealment countdown if the conditions are no longer met.
            if (sneakTimers.remove(uuid) != null) {
                player.sendSystemMessage(
                        Component.literal(
                                "[Living Shadow] Dissolving canceled!"
                        ).withStyle(ChatFormatting.RED),
                        false
                );
            }
        }
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = player.level();
        Vec3 pos = player.position();

        // Spawn a large cloud of shadow particles around the player.
        level.sendParticles(
                ParticleTypes.SQUID_INK,
                pos.x,
                pos.y + 1.5,
                pos.z,
                3000,
                6.0,
                3.0,
                6.0,
                0.0
        );

        // Spawn dark dust particles around the player.
        level.sendParticles(
                new DustParticleOptions(0x000000, 1.5F),
                pos.x,
                pos.y + 1.5,
                pos.z,
                3000,
                6.0,
                3.0,
                6.0,
                0.0
        );

        // Grants temporary speed after activating the ability.
        Effects.applyEffect(
                player,
                MobEffects.SPEED,
                200,
                2,
                false,
                false,
                false
        );

        AABB boundingBox =
                player.getBoundingBox().inflate(6);

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        // Find all living entities within the ability radius.
        List<LivingEntity> targets =
                level.getEntitiesOfClass(
                        LivingEntity.class,
                        boundingBox,
                        entity -> entity != player
                );

        for (LivingEntity target : targets) {
            // Trusted entities are ignored.
            if (data != null
                    && data.isTrusted(target.getUUID())) {
                continue;
            }

            // Blind nearby entities.
            Effects.applyEffect(
                    target,
                    MobEffects.BLINDNESS,
                    200,
                    1,
                    false,
                    false,
                    false
            );
        }
    }

    public static void handleDamageReveal(
            ServerPlayer player,
            DamageSource source
    ) {
        // Ignore damage if the player is not currently hidden.
        if (!hiddenPlayers.contains(player.getUUID())) {
            return;
        }

        // Environmental fall damage does not break concealment.
        if (source.is(DamageTypeTags.IS_FALL)) {
            return;
        }

        // Fire damage does not break concealment.
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return;
        }

        // Drowning does not break concealment.
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return;
        }

        // Being attacked by another player reveals the Shadow Core user.
        if (source.getEntity() instanceof ServerPlayer) {
            revealPlayer(player);
        }
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Cancel any active concealment countdown.
        sneakTimers.remove(uuid);

        // Remove hidden state and restore the player's equipment/effects.
        if (hiddenPlayers.remove(uuid)) {
            player.removeEffect(MobEffects.INVISIBILITY);
            player.removeEffect(MobEffects.NIGHT_VISION);

            sendFakeEquipmentPackets(player, false);
        }
    }

    public static void revealPlayer(ServerPlayer player) {
        UUID uuid = player.getUUID();

        if (hiddenPlayers.remove(uuid)) {
            // Remove the visual effects applied by Living Shadow.
            player.removeEffect(MobEffects.INVISIBILITY);
            player.removeEffect(MobEffects.NIGHT_VISION);

            // Restore the player's real equipment to tracking clients.
            sendFakeEquipmentPackets(player, false);

            player.sendSystemMessage(
                    Component.literal(
                            "[Living Shadow] Shadow concealment broken!"
                    ).withStyle(ChatFormatting.RED),
                    false
            );
        }

        // Always clear the active concealment countdown.
        sneakTimers.remove(uuid);
    }

    public static boolean isPlayerHidden(ServerPlayer player) {
        return hiddenPlayers.contains(player.getUUID());
    }

    private static void sendFakeEquipmentPackets(
            ServerPlayer player,
            boolean hide
    ) {
        List<Pair<EquipmentSlot, ItemStack>> equipment =
                new ArrayList<>();

        // Build the equipment state that should be shown to other players.
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = hide
                    ? ItemStack.EMPTY
                    : player.getItemBySlot(slot).copy();

            equipment.add(Pair.of(slot, stack));
        }

        ClientboundSetEquipmentPacket packet =
                new ClientboundSetEquipmentPacket(
                        player.getId(),
                        equipment
                );

        // Only send the packet to players currently tracking the player.
        for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
            tracker.connection.send(packet);
        }
    }
}