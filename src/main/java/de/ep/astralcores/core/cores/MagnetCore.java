package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.MagnetCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class MagnetCore extends Core {

    public MagnetCore() {
        super(
                CoreType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        "§4[Active: Magnetic Pull]"
                ),
                10012,
                0,
                0,
                "Magnetic Pull",
                "Magnetic Disarm",
                "\uE00C"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        MagnetCoreLogic.applyPassive(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        MagnetCoreLogic.onRemoved(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        MagnetCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        MagnetCoreLogic.tick(player);
    }
}