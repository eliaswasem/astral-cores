package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class IllusionCore extends Core {

    public IllusionCore() {
        super(
                CoreType.MIRROR_CORE,
                "§fMirror Shard",
                Items.AMETHYST_SHARD,
                List.of(
                        "§f[Active: Mirror Swap]"
                ),
                10011,
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
