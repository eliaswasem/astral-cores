package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.ShadowCoreLogic;
import de.ep.astralcores.core.data.CoreActivationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShadowCore extends Core {

    public ShadowCore() {
        super(
                CoreType.SHADOW_CORE,
                "§8Shadow Core",
                Items.DRAGON_BREATH,
                List.of(
                        Component.literal("A fragment of the core of a fallen meteor, consumed by darkness.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Active")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Smoke Veil")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Creates an 8-second smoke cloud. Blinds enemies and grants Speed II.")
                                .withStyle(ChatFormatting.GRAY),
                        Component.empty(),

                        Component.literal("Passive")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD, ChatFormatting.ITALIC),
                        Component.literal("Living Shadow")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                        Component.literal("Sneak in darkness for five seconds to become invisible.")
                                .withStyle(ChatFormatting.GRAY)
                ),
                10007,
                0,
                0,
                86400L,
                "Smoke Veil",
                "Living Shadow",
                "\uE008",
                BossEvent.BossBarColor.PURPLE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        ShadowCoreLogic.applyPassive(player);
    }

    @Override
    public CoreActivationResult activate(ServerPlayer player) {
        return ShadowCoreLogic.activate(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        ShadowCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        ShadowCoreLogic.onPlayerDisconnect(player);
    }
}