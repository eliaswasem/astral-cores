package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class GravityRelic extends Relic {

    public GravityRelic() {
        super(
                RelicType.GRAVITY_CORE,
                "§bGravity Core",
                Items.ARROW,
                List.of(
                        "§b[Active: Gravity Pull"
                ),
                10007,
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
