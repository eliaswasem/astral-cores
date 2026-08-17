package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.IllusionCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class IllusionCore extends Core {

    public IllusionCore() {
        super(
                CoreType.ILLUSION_CORE,
                "§fIllusion Core",
                Items.AMETHYST_SHARD,
                List.of(
                        "§f[Active: Mirror Swap]"
                ),
                10011,
                600,
                0,
                "Mirror Swap",
                "Mirror Image",
                "\uE00A"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        IllusionCoreLogic.applyPassive(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        IllusionCoreLogic.onRemoved(player);
    }

    @Override
    public void activate(ServerPlayer player) { IllusionCoreLogic.activate(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        IllusionCoreLogic.onPlayerDisconnect(player);
    }
}