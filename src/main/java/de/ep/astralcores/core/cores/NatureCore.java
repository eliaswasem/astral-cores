package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.NatureCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class NatureCore extends Core {

    public NatureCore() {
        super(
                CoreType.NATURE_CORE,
                Component.literal("Nature Core")
                        .withStyle(style -> style.withColor(0x55FF55)),
                Items.SLIME_BALL,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, awakened by the life of the world.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(style -> style
                                        .withColor(0x339933)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Root Trap")
                                .withStyle(style -> style
                                        .withColor(0xAAFFAA)
                                        .withBold(true)),
                        Component.literal("Creates a field of vines that roots nearby players.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(style -> style
                                        .withColor(0x339933)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Nature's Blessing")
                                .withStyle(style -> style
                                        .withColor(0xAAFFAA)
                                        .withBold(true)),
                        Component.literal("Natural biomes improve regeneration, speed, food healing, and crops.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10012,
                0,
                0,
                86400L,
                "Root Trap",
                "Nature Blessing",
                "\uE00B",
                BossEvent.BossBarColor.GREEN
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        NatureCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return NatureCoreLogic.activate(player);
    }
}