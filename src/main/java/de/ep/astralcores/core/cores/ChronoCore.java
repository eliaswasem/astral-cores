package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.ChronoCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class ChronoCore extends Core {

    public ChronoCore() {
        super(
                CoreType.CHRONO_CORE,
                "§aChrono Core",
                Items.CLOCK,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Time Return")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Rewinds your position by five seconds.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Second Timeline")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Survive fatal damage and fully heal. 10-minute cooldown.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10006,
                45,
                600,
                86400L,
                "Time Return",
                "Second Timeline",
                "\uE003",
                BossEvent.BossBarColor.YELLOW
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        ChronoCoreLogic.applyPassive(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        ChronoCoreLogic.onRemoved(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return ChronoCoreLogic.activate(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        ChronoCoreLogic.onPlayerDisconnect(player);
    }
}
