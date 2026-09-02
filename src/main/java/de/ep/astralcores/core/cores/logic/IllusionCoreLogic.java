package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.mixin.MannequinAccessor;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

public final class IllusionCoreLogic {

    private static final double TRIGGER_CHANCE = 0.20;
    private static final int MANNEQUIN_LIFETIME = 30 * 20;

    /**
     * Mannequins grouped by player UUID.
     */
    private static final Map<UUID, Set<MannequinData>> mannequins = new HashMap<>();

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {

        Set<MannequinData> playerMannequins =
                mannequins.remove(player.getUUID());

        if (playerMannequins == null) {
            return;
        }

        for (MannequinData data : playerMannequins) {
            Mannequin mannequin = data.mannequin();

            if (!mannequin.isRemoved()) {
                mannequin.discard();
            }
        }

        playerMannequins.clear();
    }

    /**
     * Called every server tick.
     */
    public static void tick() {
        Iterator<Map.Entry<UUID, Set<MannequinData>>> playerIterator =
                mannequins.entrySet().iterator();

        while (playerIterator.hasNext()) {
            Map.Entry<UUID, Set<MannequinData>> entry =
                    playerIterator.next();

            Set<MannequinData> playerMannequins = entry.getValue();

            playerMannequins.removeIf(data -> {
                Mannequin mannequin = data.mannequin();

                // Already dead/removed -> stop tracking it.
                if (mannequin.isRemoved() || !mannequin.isAlive()) {
                    return true;
                }

                // Five seconds elapsed -> remove mannequin.
                if (data.timer().tick()) {
                    mannequin.discard();
                    return true;
                }

                return false;
            });

            // No mannequins left for this player.
            if (playerMannequins.isEmpty()) {
                playerIterator.remove();
            }
        }
    }

    public static void activate(ServerPlayer player) {
        ServerLevel level = player.level();

        Mannequin mannequin = Mannequin.create(
                EntityTypes.MANNEQUIN,
                level
        );

        // Copy the player's position and rotation.
        mannequin.setPos(
                player.getX(),
                player.getY(),
                player.getZ()
        );

        mannequin.setYRot(player.getYRot());
        mannequin.setXRot(player.getXRot());

        // Copy the player's current health.
        mannequin.setHealth(player.getHealth());

        // Copy the player's skin and profile.
        mannequin.setComponent(
                DataComponents.PROFILE,
                ResolvableProfile.createResolved(
                        player.getGameProfile()
                )
        );

        // Set custom Name
        mannequin.setCustomName(player.getDisplayName());
        mannequin.setCustomNameVisible(true);

        ((MannequinAccessor) mannequin)
                .astralcores$setHideDescription(true);

        // Copy all equipped items.
        mannequin.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD).copy());
        mannequin.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST).copy());
        mannequin.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS).copy());
        mannequin.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET).copy());
        mannequin.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND).copy());
        mannequin.setItemSlot(EquipmentSlot.OFFHAND, player.getItemBySlot(EquipmentSlot.OFFHAND).copy());

        // Add the mannequin to the world.
        level.addFreshEntity(mannequin);

        // Track mannequin for this player.
        mannequins
                .computeIfAbsent(
                        player.getUUID(),
                        ignored -> new HashSet<>()
                )
                .add(
                        new MannequinData(
                                mannequin,
                                new TickTimer(MANNEQUIN_LIFETIME)
                        )
                );

        Effects.applyEffect(
                player,
                MobEffects.INVISIBILITY,
                100,
                1
        );

        teleportSafelyAway(player);
    }

    /**
     * Handles the passive Mirror Image effect.
     *
     * @return true if normal damage processing should continue,
     *         false if the attack should be cancelled.
     */
    public static boolean handleMirrorImage(
            ServerPlayer player,
            DamageSource source
    ) {
        if (!(AstralCores.PLAYER_DATA.get(player).getEquippedCore() == CoreType.CHRONO_CORE)) {
            return true;
        }

        // Mirror Image only reacts to direct player attacks.
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            return true;
        }

        // 20% chance to trigger.
        if (player.getRandom().nextDouble() >= TRIGGER_CHANCE) {
            return true;
        }

        // Find the attacker directly from the damage source.
        if (source.getEntity() instanceof ServerPlayer attacker) {

            Effects.applyEffect(
                    attacker,
                    MobEffects.BLINDNESS,
                    5,
                    255,
                    false,
                    false,
                    false
            );

            attacker.sendSystemMessage(
                    Component.literal("You are distracted!")
                            .withStyle(ChatFormatting.WHITE)
            );
        }

        // Attack is completely dodged.
        return false;
    }

    private record MannequinData(
            Mannequin mannequin,
            TickTimer timer
    ) {
    }
    private static void teleportSafelyAway(ServerPlayer player) {
        ServerLevel level = player.level();

        for (int i = 0; i < 20; i++) {
            double distance = 10 + player.getRandom().nextDouble() * 5;
            double angle = player.getRandom().nextDouble() * Math.PI * 2;

            int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);

            int y = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x,
                    z
            );

            BlockPos pos = new BlockPos(x, y, z);

            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    || !level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
                continue;
            }

            player.teleportTo(x + 0.5, y, z + 0.5);
            return;
        }
    }
}