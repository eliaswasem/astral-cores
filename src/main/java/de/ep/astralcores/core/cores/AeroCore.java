package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class AeroCore extends Core {

    // Tracks which players currently have this core's passive effect active
    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public AeroCore() {
        super(
                CoreType.AERO_CORE,
                "§bAero Core",
                Items.FEATHER,
                List.of(
                        "§7Forged in shifting air currents.",
                        "§6[Active: Tornado Lift]"
                ),
                10001,
                15,
                0,
                "Tornado Lift",
                "Featherweight",
                "\uE001"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        // Just add the player to the active map
        activePlayers.add(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        // Just remove the player from the active map
        activePlayers.remove(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        // Will be implemented later
    }
}
