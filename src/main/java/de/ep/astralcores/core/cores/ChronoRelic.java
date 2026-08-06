package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class ChronoRelic extends Core {

    public ChronoRelic() {
        super(
                CoreType.CHRONO_RELIC,
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
