package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.FrostCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public final class FrostCore extends Core {

    public FrostCore() {
        super(
                CoreType.FROST_CORE,
                "§bFrost Core",
                Items.CLAY_BALL,
                List.of(
                        "§b[Passive: Frost Aura]",
                        "§b[Active: Frost Lock]"
                ),
                10008,
                0,
                0,
                "Frost Lock",
                "Frost Aura",
                "\uE005"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        FrostCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        FrostCoreLogic.activate(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        FrostCoreLogic.onRemoved(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        FrostCoreLogic.tick(player);
    }

}