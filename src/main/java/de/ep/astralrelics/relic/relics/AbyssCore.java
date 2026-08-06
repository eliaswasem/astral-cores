package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class AbyssCore extends Relic {

    public AbyssCore() {
        super(
                RelicType.ABYSS_CORE,
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
