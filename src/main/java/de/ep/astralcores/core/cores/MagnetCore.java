package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.MagnetCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class MagnetCore extends Core {

    public MagnetCore() {
        super(
                CoreType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, radiating magnetic force.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Magnetic Pull")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Pulls nearby items and XP orbs directly toward you.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Magnetic Disarm")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Critical hits can freeze an enemy's attack for 1.5 seconds.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10012,
                0,
                0,
                86400L,
                "Magnetic Pull",
                "Magnetic Disarm",
                "\uE00C",
                BossEvent.BossBarColor.WHITE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        MagnetCoreLogic.applyPassive(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        MagnetCoreLogic.onRemoved(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return MagnetCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        MagnetCoreLogic.tick(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        MagnetCoreLogic.onPlayerDisconnect(player);
    }
}