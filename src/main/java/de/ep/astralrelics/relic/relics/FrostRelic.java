package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class FrostRelic extends Relic {

    public FrostRelic() {
        super(
                RelicType.FROST_RELIC,
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
