package de.ep.astralcores.manager;

import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.server.level.ServerPlayer;

public class CoreTickManager {

    // Triggers the passive capability logic for the player's equipped core
    public static void tickPassiveAbility(ServerPlayer player, PlayerData data) {
        if (data == null) return;

        if (data.getEquippedCore() != null) {
            CoreRegistry.get(data.getEquippedCore()).ifPresent(core -> {
                core.applyPassive(player);
            });
        }
    }

    // Triggers the active constant ticking update loop for the player's equipped core
    public static void tick(ServerPlayer player, PlayerData data) {
        if (data == null) return;

        if (data.getEquippedCore() != null) {
            CoreRegistry.get(data.getEquippedCore()).ifPresent(core -> {
                core.tick(player);
            });
        }
    }
}
