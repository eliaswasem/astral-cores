package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class NatureCore extends Core {

    public NatureCore() {
        super(
                CoreType.NATURE_CORE,
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
