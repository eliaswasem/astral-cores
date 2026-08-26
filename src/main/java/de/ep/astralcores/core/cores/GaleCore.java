package de.ep.astralcores.core.cores;

import java.util.List;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.GaleCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

public class GaleCore extends Core {


    public GaleCore() {
        super(
                CoreType.GALE_CORE,
                "§7Gale Core",
                Items.BREEZE_ROD,
                List.of(
                        "§f[Active: Sonic Dash]"
                ),
                10005,
                0,
                0,
                "Sonic Dash",
                "Lightfeet",
                "\uE002"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        GaleCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        GaleCoreLogic.activate(player);
    }

    @Override
    public void tick(ServerPlayer player) {
        GaleCoreLogic.tick(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        GaleCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        GaleCoreLogic.onPlayerDisconnect(player);
    }
}
