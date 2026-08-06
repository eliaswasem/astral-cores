package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class GaleRelic extends Relic {

    public GaleRelic() {
        super(
                RelicType.GALE_RELIC,
                "§7Gale Core",
                Items.BREEZE_ROD,
                List.of(
                        "§f[Active: Sonic Dash]"
                ),
                10005,
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
