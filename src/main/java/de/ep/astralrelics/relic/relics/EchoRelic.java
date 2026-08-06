package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class EchoRelic extends Relic {

    public EchoRelic() {
        super(
                RelicType.ECHO_CORE,
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

