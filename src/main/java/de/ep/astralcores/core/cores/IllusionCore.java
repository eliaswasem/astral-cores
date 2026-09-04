package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.IllusionCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class IllusionCore extends Core {

    public IllusionCore() {
        super(
                CoreType.ILLUSION_CORE,
                "§fIllusion Core",
                Items.AMETHYST_SHARD,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, distorting light and perception.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Mirror Swap")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Creates a decoy, grants invisibility, and teleports you 10–15 blocks away.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Mirror Image")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Being hit has a chance to create an illusion.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10011,
                600,
                0,
                86400L,
                "Mirror Swap",
                "Mirror Image",
                "\uE00A",
                BossEvent.BossBarColor.PURPLE
        );
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        IllusionCoreLogic.onRemoved(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return IllusionCoreLogic.activate(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        IllusionCoreLogic.onPlayerDisconnect(player);
    }
}