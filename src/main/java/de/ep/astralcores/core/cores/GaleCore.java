package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.GaleCoreLogic;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GaleCore extends Core {

    // Stores the explosion timer for each player currently using Sonic Dash.
    private static final Map<UUID, TickTimer> explosionTimers = new HashMap<>();

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
}