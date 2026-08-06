package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class GravityShard extends Relic {

    public GravityShard() {
        super(
                RelicType.GRAVITY_SHARD,
                "§bGravity Shard",
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
