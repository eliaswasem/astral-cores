package de.ep.astralcores.manager;

import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;

public class CooldownManager {

    /**
     * Updates active and passive core cooldown states for a specific player context.
     * Invoked sequentially within the centralized server main loop tick handler.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param data   The cached PlayerData reference provided by the central main loop.
     */
    public static void tick(ServerPlayer player, PlayerData data) {
        if (data != null) {
            data.tickCooldowns();
        }
    }

    /**
     * Evaluates whether the active capability of a specific core type is ready for use.
     */
    public static boolean isActiveReady(PlayerData data, CoreType type) {
        return data == null || getActiveRemaining(data, type) <= 0;
    }

    /**
     * Evaluates whether the passive capability of a specific core type is ready for use.
     */
    public static boolean isPassiveReady(PlayerData data, CoreType type) {
        return data == null || getPassiveRemaining(data, type) <= 0;
    }

    /**
     * Retrieves the remaining active cooldown duration metrics for a specific core type mapping.
     * Queries the map directly using the new data layer getters.
     */
    public static int getActiveRemaining(PlayerData data, CoreType type) {
        return data != null ? data.getActiveCooldownsMap().getOrDefault(type, 0) : 0;
    }

    /**
     * Retrieves the remaining passive cooldown duration metrics for a specific core type mapping.
     * Queries the map directly using the new data layer getters.
     */
    public static int getPassiveRemaining(PlayerData data, CoreType type) {
        return data != null ? data.getPassiveCooldownsMap().getOrDefault(type, 0) : 0;
    }

    /**
     * Forces a localized active capability lock boundary onto the target player profile.
     * Modifies the backing map directly, purging keys if the duration hits zero.
     */
    public static void startActiveCooldown(PlayerData data, CoreType type, int seconds) {
        if (data == null) return;

        if (seconds <= 0) {
            data.getActiveCooldownsMap().remove(type);
        } else {
            data.getActiveCooldownsMap().put(type, seconds);
        }
    }

    /**
     * Forces a localized passive capability lock boundary onto the target player profile.
     * Modifies the backing map directly, purging keys if the duration hits zero.
     */
    public static void startPassiveCooldown(PlayerData data, CoreType type, int seconds) {
        if (data == null) return;

        if (seconds <= 0) {
            data.getPassiveCooldownsMap().remove(type);
        } else {
            data.getPassiveCooldownsMap().put(type, seconds);
        }
    }
}
