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

    // Weak mappings allow player state to be garbage-collected once the ServerPlayer is no longer strongly referenced.
    private static final Map<ServerPlayer, Integer> sneakTimers =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static final Set<ServerPlayer> hiddenPlayers =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final int MAX_LIGHT_LEVEL = 0;

    // Threshold configured directly in seconds because applyPassive fires once per second.
    private static final int TIME_THRESHOLD_SECONDS = 5;

    private ShadowCoreLogic() {
    }

    public static void applyPassive(ServerPlayer player) {
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
        if (hiddenPlayers.contains(player)) {
            // Keep duration at 300 ticks (15s) to stay above the 200 tick (10s) flicker limit!
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
            // Increments cleanly by 1 second on every execution cycle.
            int secondsPassed = sneakTimers.getOrDefault(player, 0) + 1;
            sneakTimers.put(player, secondsPassed);

            // The player has crouched in darkness for the required duration (5 seconds).
            if (secondsPassed >= TIME_THRESHOLD_SECONDS) {
                hiddenPlayers.add(player);
                sneakTimers.remove(player);

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
                // Calculate real remaining seconds accurately.
                int remainingSeconds = TIME_THRESHOLD_SECONDS - secondsPassed;

                // Passing 'true' prints this cleanly to the Action Bar instead of clogging chat history.
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
            // Cancel the concealment countdown if the player stops crouching
            // or the environment is no longer dark enough.
            if (sneakTimers.remove(player) != null) {
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

        // Spawn particels
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

        // Spawn particels
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

        Effects.applyEffect(player, MobEffects.SPEED, 200, 2, false, false, false);

        AABB boundingBox = player.getBoundingBox().inflate(6);

        PlayerData data = AstralCores.PLAYER_DATA.get(player);


        List<LivingEntity> targets =
                level.getEntitiesOfClass(
                        LivingEntity.class,
                        boundingBox,
                        entity -> entity != player
                );
        for (LivingEntity target : targets) {
            if (data != null && data.isTrusted(target.getUUID())) {
                continue;
            }
            Effects.applyEffect(target, MobEffects.BLINDNESS, 200, 1, false, false, false);
        }
    }
    public static void tick(ServerPlayer player) {
        // Shadow Core currently has no separate per-tick active ability.
    }

    public static void handleDamageReveal(
            ServerPlayer player,
            DamageSource source
    ) {
        // Ignore damage if the player is not currently hidden.
        if (!hiddenPlayers.contains(player)) {
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
        // Cancel any concealment countdown when the core is removed.
        sneakTimers.remove(player);

        // Clean up all hidden-state effects and restore the player's equipment.
        if (hiddenPlayers.remove(player)) {
            player.removeEffect(MobEffects.INVISIBILITY);
            player.removeEffect(MobEffects.NIGHT_VISION);

            sendFakeEquipmentPackets(player, false);
        }
    }

    public static void revealPlayer(ServerPlayer player) {
        if (hiddenPlayers.remove(player)) {
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
        sneakTimers.remove(player);
    }

    public static boolean isPlayerHidden(ServerPlayer player) {
        return hiddenPlayers.contains(player);
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
