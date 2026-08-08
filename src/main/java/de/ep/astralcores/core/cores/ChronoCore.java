package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class ChronoCore extends Core {

    public ChronoCore() {
        super(
                CoreType.CHRONO_CORE,
                "$aChrono Core",
                Items.CLOCK,
                List.of(
                        "§a[Active: Time Return]"
                ),
                10006,
                0,
                0,
                "Time Return",
                "Second Timeline"
        );
    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
