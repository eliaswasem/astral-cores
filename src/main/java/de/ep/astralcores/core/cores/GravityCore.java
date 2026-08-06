package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class GravityCore extends Core {

    public GravityCore() {
        super(
                CoreType.GRAVITY_CORE,
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
