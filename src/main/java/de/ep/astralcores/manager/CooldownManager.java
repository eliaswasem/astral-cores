package de.ep.astralcores.manager;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CooldownManager {

    /**
     * Iterates through all currently connected network player entities
     * and updates their active and passive core cooldown states.
     * This method is triggered continuously by the low-frequency loop handler.
     *
     * @param server The MinecraftServer context providing the player list.
     */
    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerData data = AstralCores.PLAYER_DATA.get(player);
            if (data != null) {
                data.tickCooldowns();
            }
        }
    }

    /**
     * Evaluates whether the active capability of a specific core type is ready for use.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param type   The CoreType registration module key to check.
     * @return True if no active cooldown lock is running, false otherwise.
     */
    public static boolean isActiveReady(ServerPlayer player, CoreType type) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        return data == null || data.getActiveCooldownSeconds(type) <= 0;
    }

    /**
     * Evaluates whether the passive capability of a specific core type is ready for use.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param type   The CoreType registration module key to check.
     * @return True if no passive cooldown lock is running, false otherwise.
     */
    public static boolean isPassiveReady(ServerPlayer player, CoreType type) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        return data == null || data.getPassiveCooldownSeconds(type) <= 0;
    }

    /**
     * Retrieves the remaining active cooldown duration metrics for a specific core type mapping.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param type   The CoreType registration module key to read.
     * @return The exact remaining cooldown interval metric tracker in seconds.
     */
    public static int getActiveRemaining(ServerPlayer player, CoreType type) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        return data != null ? data.getActiveCooldownSeconds(type) : 0;
    }

    /**
     * Retrieves the remaining passive cooldown duration metrics for a specific core type mapping.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param type   The CoreType registration module key to read.
     * @return The exact remaining cooldown interval metric tracker in seconds.
     */
    public static int getPassiveRemaining(ServerPlayer player, CoreType type) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        return data != null ? data.getPassiveCooldownSeconds(type) : 0;
    }

    /**
     * Forces a localized active capability lock boundary onto the target player profile.
     *
     * @param player  The target ServerPlayer entity receiving the constraint.
     * @param type    The CoreType module category tracking the lock node.
     * @param seconds The threshold boundary tracking limit duration in seconds.
     */
    public static void startActiveCooldown(ServerPlayer player, CoreType type, int seconds) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data != null) {
            data.setActiveCooldownSeconds(type, seconds);
        }
    }

    /**
     * Forces a localized passive capability lock boundary onto the target player profile.
     *
     * @param player  The target ServerPlayer entity receiving the constraint.
     * @param type    The CoreType module category tracking the lock node.
     * @param seconds The threshold boundary tracking limit duration in seconds.
     */
    public static void startPassiveCooldown(ServerPlayer player, CoreType type, int seconds) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data != null) {
            data.setPassiveCooldownSeconds(type, seconds);
        }
    }
}
