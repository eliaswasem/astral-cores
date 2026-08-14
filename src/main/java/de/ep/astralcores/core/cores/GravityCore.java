package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.GravityCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public final class GravityCore extends Core {

    public GravityCore() {
        super(
                CoreType.GRAVITY_CORE,
                "§5Gravity Core",
                Items.HEAVY_CORE,
                List.of(
                        "§7Control the fabric of mass.",
                        "§6[Active: Gravity Pull]"
                ),
                10004,
                25,
                0,
                "Gravity Pull",
                "Heavy Presence",
                "\uE004"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        GravityCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        GravityCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        GravityCoreLogic.tick(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        GravityCoreLogic.onRemoved(player);
    }
}