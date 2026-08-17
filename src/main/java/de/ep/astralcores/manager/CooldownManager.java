package de.ep.astralcores.manager;

import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;

public class CooldownManager {

    // Decrements active and passive cooldown duration metrics inside the player profile data structures
    public static void tick(ServerPlayer player, PlayerData data) {
        if (data != null) {
            data.tickCooldowns();
        }
    }

    // Checks if the active ability of a specific core type is ready for execution
    public static boolean isActiveReady(PlayerData data, CoreType type) {
        return data == null || getActiveRemaining(data, type) <= 0;
    }

    // Checks if the passive utility of a specific core type is ready for execution
    public static boolean isPassiveReady(PlayerData data, CoreType type) {
        return data == null || getPassiveRemaining(data, type) <= 0;
    }

    // Gets the remaining active capability cooldown duration in seconds
    public static int getActiveRemaining(PlayerData data, CoreType type) {
        return data != null ? data.getActiveCooldownsMap().getOrDefault(type, 0) : 0;
    }

    // Gets the remaining passive recovery capability cooldown duration in seconds
    public static int getPassiveRemaining(PlayerData data, CoreType type) {
        return data != null ? data.getPassiveCooldownsMap().getOrDefault(type, 0) : 0;
    }

    // Sets or clears the active capability cooldown interval for a specific core type
    public static void startActiveCooldown(PlayerData data, CoreType type, int seconds) {
        if (data == null) return;

        if (seconds <= 0) {
            data.getActiveCooldownsMap().remove(type);
        } else {
            data.getActiveCooldownsMap().put(type, seconds);
        }
    }

    // Sets or clears the passive ability cooldown interval for a specific core type
    public static void startPassiveCooldown(PlayerData data, CoreType type, int seconds) {
        if (data == null) return;

        if (seconds <= 0) {
            data.getPassiveCooldownsMap().remove(type);
        } else {
            data.getPassiveCooldownsMap().put(type, seconds);
        }
    }

    public static void resetCooldowns(PlayerData cooldown) {
            if (cooldown == null) {
                return;
            }

            cooldown.getActiveCooldownsMap().clear();
            cooldown.getPassiveCooldownsMap().clear();
    }
}
