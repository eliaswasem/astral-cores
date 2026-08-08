package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class BersekerCore extends Core {

    public BersekerCore() {
        super(
                CoreType.BERSERKER_CORE,
                "§6Berseker Core",
                Items.RABBIT,
                List.of(
                        "§6[Active: Rage Mode]"
                ),
                10011,
                0,
                0,
                "Rage Mode",
                "Bloodlust"
        );

    }

    @Override
    public void applyPassive(ServerPlayer player) {

    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
