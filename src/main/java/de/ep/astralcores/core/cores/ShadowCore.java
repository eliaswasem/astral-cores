package de.ep.astralcores.core.cores;

import com.mojang.datafixers.util.Pair;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowCore extends Core {

    private static final Map<UUID, Integer> SNEAK_TIMERS =
            new ConcurrentHashMap<>();

    private static final Set<UUID> HIDDEN_PLAYERS =
            ConcurrentHashMap.newKeySet();

    private static final int MAX_LIGHT_LEVEL = 0;
    private static final int TIME_THRESHOLD_SECONDS = 5;


    public ShadowCore() {
        super(
                CoreType.SHADOW_CORE,
                "§8Shadow Core",
                net.minecraft.world.item.Items.POTION,
                List.of("§8[Passive: Living Shadow]"),
                10007,
                0,
                0,
                "",
                ""
        );
    }


    @Override
    public void applyPassive(ServerPlayer player) {

        UUID uuid = player.getUUID();

        int blockLight =
                player.level()
                        .getBrightness(
                                LightLayer.BLOCK,
                                player.blockPosition()
                        );

        boolean isOutdoors =
                player.level()
                        .getBrightness(
                                LightLayer.SKY,
                                player.blockPosition()
                        ) > 0;

        boolean isDarkEnough =
                blockLight <= MAX_LIGHT_LEVEL
                        && (!isOutdoors || player.level().isDarkOutside());


        if (HIDDEN_PLAYERS.contains(uuid)) {

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


            if (player.isSprinting() || !isDarkEnough) {
                revealPlayer(player);
            }

            return;
        }


        if (player.isCrouching() && isDarkEnough) {

            int seconds =
                    SNEAK_TIMERS.getOrDefault(uuid, 0) + 1;

            SNEAK_TIMERS.put(uuid, seconds);


            if (seconds >= TIME_THRESHOLD_SECONDS) {

                HIDDEN_PLAYERS.add(uuid);
                SNEAK_TIMERS.remove(uuid);


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


                // REMOVE ARMOR/ITEMS VISUALLY
                sendFakeEquipmentPackets(player, true);


                player.sendSystemMessage(
                        Component.literal(
                                "[Living Shadow] You dissolved into the shadows."
                        ).withStyle(ChatFormatting.DARK_PURPLE)
                );

            } else {

                int remaining =
                        TIME_THRESHOLD_SECONDS - seconds;

                player.sendSystemMessage(
                        Component.literal(
                                "[Living Shadow] Dissolving in "
                                        + remaining
                                        + "s..."
                        ).withStyle(ChatFormatting.GRAY)
                );
            }

        } else {

            if (SNEAK_TIMERS.containsKey(uuid)) {

                player.sendSystemMessage(
                        Component.literal("[Living Shadow] Dissolving canceled!")
                                .withStyle(ChatFormatting.RED)
                );

                SNEAK_TIMERS.remove(uuid);
            }
        }
    }


    @Override
    public void activate(ServerPlayer player) {
    }


    @Override
    public void onRemoved(ServerPlayer player) {

        SNEAK_TIMERS.remove(player.getUUID());

        if (HIDDEN_PLAYERS.remove(player.getUUID())) {

            player.removeEffect(MobEffects.INVISIBILITY);
            player.removeEffect(MobEffects.NIGHT_VISION);

            // RESTORE ARMOR
            sendFakeEquipmentPackets(player, false);
        }
    }


    public static void revealPlayer(ServerPlayer player) {

        UUID uuid = player.getUUID();

        if (HIDDEN_PLAYERS.remove(uuid)) {

            player.removeEffect(MobEffects.INVISIBILITY);
            player.removeEffect(MobEffects.NIGHT_VISION);


            // RESTORE ARMOR
            sendFakeEquipmentPackets(player, false);


            player.sendSystemMessage(
                    Component.literal(
                            "[Living Shadow] Shadow concealment broken!"
                    ).withStyle(ChatFormatting.RED)
            );
        }

        SNEAK_TIMERS.remove(uuid);
    }


    public static boolean isPlayerHidden(UUID uuid) {
        return HIDDEN_PLAYERS.contains(uuid);
    }


    private static void sendFakeEquipmentPackets(
            ServerPlayer player,
            boolean hide
    ) {

        List<Pair<EquipmentSlot, ItemStack>> equipment =
                new ArrayList<>();


        for (EquipmentSlot slot : EquipmentSlot.values()) {

            ItemStack stack =
                    hide
                            ? ItemStack.EMPTY
                            : player.getItemBySlot(slot).copy();


            equipment.add(
                    Pair.of(slot, stack)
            );
        }


        ClientboundSetEquipmentPacket packet =
                new ClientboundSetEquipmentPacket(
                        player.getId(),
                        equipment
                );


        for (ServerPlayer tracker :
                PlayerLookup.tracking(player)) {

            tracker.connection.send(packet);
        }
    }
}