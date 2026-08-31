package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.PhoenixCoreLogic;
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
                        "§7Forged in cosmic radiation.",
                        "§6[Active: Phoenix Burst]"
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
    public void activate(ServerPlayer player) {
        PhoenixCoreLogic.activate(player);
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