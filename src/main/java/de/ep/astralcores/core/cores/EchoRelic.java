package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class EchoRelic extends Core {

    public EchoRelic() {
        super(
                CoreType.ECHO_RELIC,
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

