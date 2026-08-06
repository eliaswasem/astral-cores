package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class FrostRelic extends Core {

    public FrostRelic() {
        super(
                CoreType.FROST_RELIC,
                "§bFrost Relict",
                Items.CLAY_BALL,
                List.of(
                        "§b[Active: Frost Lock]"
                ),
                10008,
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
