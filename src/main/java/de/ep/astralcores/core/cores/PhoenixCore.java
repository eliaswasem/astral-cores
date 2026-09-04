package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.PhoenixCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class PhoenixCore extends Core {

    public PhoenixCore() {
        super(
                CoreType.PHOENIX_CORE,
                "§cPhoenix Core",
                Items.MAGMA_CREAM,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, burning with an eternal flame.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Phoenix Burst")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Fiery explosion that burns and knocks back nearby enemies.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Flameborn")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Immune to fire and lava. Regeneration I in the Nether.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10003,
                30,
                0,
                86400L,
                "Phoenix Burst",
                "Flameborn",
                "\uE006",
                BossEvent.BossBarColor.RED
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        PhoenixCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return PhoenixCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        PhoenixCoreLogic.tick(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        PhoenixCoreLogic.onRemoved(player);
    }
}