package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShadowCore extends Core {

    public ShadowCore() {
        super(
                CoreType.SHADOW_CORE,
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
