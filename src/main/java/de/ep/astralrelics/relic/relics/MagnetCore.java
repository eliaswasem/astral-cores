package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class MagnetCore extends Relic {

    public MagnetCore() {
        super(
                RelicType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        "§4[Active: Magnetic Pull]"
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
