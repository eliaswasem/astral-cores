package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class NatureRelic extends Relic {

    public NatureRelic() {
        super(
                RelicType.NATURE_CORE,
                "§2Nature Core",
                Items.BOWL,
                List.of(
                        "§2[Active: Root Trap]"
                ),
                10012,
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
