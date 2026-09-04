package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.ShadowCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShadowCore extends Core {

    public ShadowCore() {
        super(
                CoreType.SHADOW_CORE,
                Component.literal("Shadow Core")
                        .withStyle(style -> style.withColor(0x5500AA)),
                Items.DRAGON_BREATH,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, consumed by darkness.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(style -> style
                                        .withColor(0x2A0055)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Smoke Veil")
                                .withStyle(style -> style
                                        .withColor(0xAA55FF)
                                        .withBold(true)),
                        Component.literal("Creates an 8-second smoke cloud. Blinds enemies and grants Speed II.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(style -> style
                                        .withColor(0x2A0055)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Living Shadow")
                                .withStyle(style -> style
                                        .withColor(0xAA55FF)
                                        .withBold(true)),
                        Component.literal("Sneak in darkness for five seconds to become invisible.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10007,
                0,
                0,
                86400L,
                "Smoke Veil",
                "Living Shadow",
                "\uE008",
                BossEvent.BossBarColor.PURPLE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        ShadowCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return ShadowCoreLogic.activate(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        ShadowCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        ShadowCoreLogic.onPlayerDisconnect(player);
    }
}