package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.AeroCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class AeroCore extends Core {
    public AeroCore() {
        super(
                CoreType.AERO_CORE,
                Component.literal("Aero Core")
                        .withStyle(style -> style.withColor(0x5555FF)),
                Items.FEATHER,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(style -> style
                                        .withColor(0x333399)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Tornado Lift")
                                .withStyle(style -> style
                                        .withColor(0x9999FF)
                                        .withBold(true)),
                        Component.literal("Launches nearby enemies into the air with a violent updraft.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(style -> style
                                        .withColor(0x333399)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Featherweight")
                                .withStyle(style -> style
                                        .withColor(0x9999FF)
                                        .withBold(true)),
                        Component.literal("Immune to fall damage. Landing damages nearby enemies.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10001,
                45,
                0,
                86400L,
                "Tornado Lift",
                "Featherweight",
                "\uE001",
                BossEvent.BossBarColor.BLUE
        );
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AeroCoreLogic.onRemoved(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return AeroCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        AeroCoreLogic.tick(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        AeroCoreLogic.onPlayerDisconnect(player);
    }
}