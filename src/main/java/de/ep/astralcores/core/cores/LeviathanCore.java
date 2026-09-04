package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.LeviathanCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class LeviathanCore extends Core {

    public LeviathanCore() {
        super(
                CoreType.LEVIATHAN_CORE,
                Component.literal("Leviathan Core")
                        .withStyle(style -> style.withColor(0x0055FF)),
                Items.TIPPED_ARROW,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, awakened beneath the deepest waters.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(style -> style
                                        .withColor(0x003399)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Whirlpool")
                                .withStyle(style -> style
                                        .withColor(0x55AAFF)
                                        .withBold(true)),
                        Component.literal("Creates a vortex that pulls nearby players inward if you are in water or rain. Twice as strong underwater.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(style -> style
                                        .withColor(0x003399)
                                        .withBold(true)
                                        .withItalic(true)),
                        Component.literal("Oceanborn")
                                .withStyle(style -> style
                                        .withColor(0x55AAFF)
                                        .withBold(true)),
                        Component.literal("Improved swimming and infinite water breathing. Water or Rain grants additional buffs.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10009,
                0,
                0,
                86400L,
                "Whirlpool",
                "Oceanborn",
                "\uE007",
                BossEvent.BossBarColor.BLUE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        LeviathanCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return LeviathanCoreLogic.activate(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        LeviathanCoreLogic.onPlayerDisconnect(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        LeviathanCoreLogic.tick(player);
    }
}