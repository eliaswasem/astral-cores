package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class AeroCore extends Core {

    public AeroCore() {
        super(
                CoreType.ECHO_CORE,
                "§9Echo Core",
                Items.HEART_OF_THE_SEA,
                List.of(
                        "§1[Active: Echo Jump]"
                ),
                10004,
                0,
                0
        );

    }

    @Override
    public void applyPassive(ServerPlayer player) {

    }

    @Override
    public void activate(ServerPlayer player) {

    }
}

