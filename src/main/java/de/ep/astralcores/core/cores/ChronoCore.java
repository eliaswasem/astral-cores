package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.WeakHashMap;

public class ChronoCore extends Core {

    // Tracks which players currently have this core's passive effect active
    public static final WeakHashMap<ServerPlayer, Boolean> activePlayers = new WeakHashMap<>();

    public ChronoCore() {
        super(
                CoreType.CHRONO_CORE,
                "§aChrono Core",
                Items.CLOCK,
                List.of(
                        "§a[Active: Time Return]"
                ),
                10006,
                0,
                0,
                "Time Return",
                "Second Timeline",
                "\uE003"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        // Marks this player as having the core active
        activePlayers.put(player, true);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        // Removes the player from the active map when unequipped
        activePlayers.remove(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        // Will be implemented later
    }
}
