package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.GravityCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public final class GravityCore extends Core {

    public GravityCore() {
        super(
                CoreType.GRAVITY_CORE,
                "§5Gravity Core",
                Items.NETHERITE_INGOT,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Gravity Pull")
                                .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD),
                        Component.literal("Pulls nearby players toward you.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Heavy Presence")
                                .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD),
                        Component.literal("50% knockback resistance.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10004,
                25,
                0,
                86400L,
                "Gravity Pull",
                "Heavy Presence",
                "\uE004",
                BossEvent.BossBarColor.WHITE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        GravityCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return GravityCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        GravityCoreLogic.tick(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        GravityCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        GravityCoreLogic.onPlayerDisconnect(player);
    }
}