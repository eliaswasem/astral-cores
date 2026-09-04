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
                "§2Nature Core",
                Items.SLIME_BALL,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, awakened by the life of the world.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Root Trap")
                                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD),
                        Component.literal("Creates a field of vines that roots nearby players.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Nature's Blessing")
                                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD),
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