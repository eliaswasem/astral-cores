package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.AeroCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class AeroCore extends Core {
    public AeroCore() {
        super(
                CoreType.AERO_CORE,
                "§bAero Core",
                Items.FEATHER,
                List.of(
                        "§7Forged in shifting air currents.",
                        "§6[Active: Tornado Lift]"
                ),
                10001,
                15,
                0,
                86400L,
                "Tornado Lift",
                "Featherweight",
                "\uE001",
                BossEvent.BossBarColor.BLUE
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        AeroCoreLogic.applyPassive(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AeroCoreLogic.onRemoved(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        AeroCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        AeroCoreLogic.tick(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        AeroCoreLogic.onPlayerDisconnect(player);
    }
}

