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
                "§1Leviathan Core",
                Items.TIPPED_ARROW,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, awakened beneath the deepest waters.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Whirlpool")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                        Component.literal("Creates a vortex that pulls nearby players inward if you are in water or rain. Twice as strong underwater.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Oceanborn")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
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
        return LeviathanCoreLogic.activate(player);}

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        LeviathanCoreLogic.onPlayerDisconnect(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        LeviathanCoreLogic.tick(player);
    }
}
