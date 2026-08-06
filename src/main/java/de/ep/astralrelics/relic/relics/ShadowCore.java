package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShadowCore extends Relic {

    public ShadowCore() {
        super(
                RelicType.SHADOW_CORE,
                "§0Shadow Core",
                Items.COMMAND_BLOCK,
                List.of(
                        "§0[Active: Smoke Veil]"
                ),
                10010,
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
