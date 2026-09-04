package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.BerserkerCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class BerserkerCore extends Core {

    public BerserkerCore() {
        super(
                CoreType.BERSERKER_CORE,
                "§6Berseker Core",
                Items.BLAZE_POWDER,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, burning with raw destructive force.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Rage Mode")
                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        Component.literal("Gain +15% melee damage, Strength II, Speed II and Fire Resistance.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Bloodlust")
                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        Component.literal("Kills restore 4 hearts and grant Speed II and Strength III.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10011,
                300,
                0,
                86400L,
                "Rage Mode",
                "Bloodlust",
                "\uE009",
                BossEvent.BossBarColor.RED
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        BerserkerCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return BerserkerCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        BerserkerCoreLogic.tick(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        BerserkerCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        BerserkerCoreLogic.onPlayerDisconnect(player);
    }
}
