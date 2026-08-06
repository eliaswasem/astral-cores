package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class ChronoRelic extends Relic {

    public ChronoRelic() {
        super(
                RelicType.CHRONO_CORE,
                "$aChrono Core",
                Items.BOOK,
                List.of(
                        "§a[Active: Time Return]"
                ),
                10006,
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
