package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class LeviathanCore extends Core {

    public LeviathanCore() {
        super(
                CoreType.ABYSS_CORE,
                "§1Abyss Core",
                Items.TIPPED_ARROW,
                List.of(
                        "§1[Active: Whirlpool]"
                ),
                10009,
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
