package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.FrostCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public final class FrostCore extends Core {

    public FrostCore() {
        super(
                CoreType.FROST_CORE,
                "§bFrost Core",
                Items.SNOWBALL,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Frost Lock")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                        Component.literal("Freezes a target in place for five seconds.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Frost Aura")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                        Component.literal("Slows nearby enemies. Stronger at close range.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10008,
                30,
                0,
                86400L,
                "Frost Lock",
                "Frost Aura",
                "\uE005",
                BossEvent.BossBarColor.BLUE
        );
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return FrostCoreLogic.activate(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        FrostCoreLogic.onRemoved(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        FrostCoreLogic.tick(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        FrostCoreLogic.onPlayerDisconnect(player);
    }
}