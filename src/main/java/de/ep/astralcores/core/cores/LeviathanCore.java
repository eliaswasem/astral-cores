package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.LeviathanCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class LeviathanCore extends Core {

    public LeviathanCore() {
        super(
                CoreType.LEVIATHAN_CORE,
                "§1Leviathan Core",
                Items.TIPPED_ARROW,
                List.of(
                        "§1[Active: Whirlpool]"
                ),
                10009,
                0,
                0,
                "Whirlpool",
                "Oceanborn",
                "\uE007"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        LeviathanCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {LeviathanCoreLogic.activate(player);}
}
