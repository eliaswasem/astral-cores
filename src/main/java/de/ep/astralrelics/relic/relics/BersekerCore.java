package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class BersekerCore extends Relic {

    public BersekerCore() {
        super(
                RelicType.BERSERKER_RELICT,
                "§6Berseker Core",
                Items.RABBIT,
                List.of(
                        "§6[Active: Rage Mode]"
                ),
                10011,
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
