package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.Config;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;

public class CoreDeathLogic {

    /**
     * Handles core retention or loss logic when a player dies based on the configured enum behavior.
     * Instantly clears passive status effects/attribute buffers via onRemoved for lost cores.
     *
     * @param player The dying ServerPlayer entity containing the active slot configurations.
     */
    public static void executeDeathDrop(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) return;

        Config.DeathBehavior behavior = ConfigManager.get().general.core_death_behavior;

        // Case 1: NONE -> Player keeps everything, no action required
        if (behavior == Config.DeathBehavior.NONE) {
            return;
        }

        // Case 2: RANDOM -> Only one core is stripped from the slots randomly
        if (behavior == Config.DeathBehavior.RANDOM) {
            boolean hasLeft = data.getLeftCore() != null;
            boolean hasRight = data.getRightCore() != null;

            if (hasLeft && hasRight) {
                if (player.getRandom().nextBoolean()) {
                    clearSlot(player, data, true);
                } else {
                    clearSlot(player, data, false);
                }
            } else if (hasLeft) {
                clearSlot(player, data, true);
            } else if (hasRight) {
                clearSlot(player, data, false);
            }
            return;
        }

        // Case 3: ALL -> Both active data slots are completely wiped
        if (behavior == Config.DeathBehavior.ALL) {
            clearSlot(player, data, true);
            clearSlot(player, data, false);
        }
    }

    /**
     * Helper method to trigger the onRemoved hook for long-running status buffers and clear the data slot.
     */
    private static void clearSlot(ServerPlayer player, PlayerData data, boolean isLeftSlot) {
        CoreType type = isLeftSlot ? data.getLeftCore() : data.getRightCore();
        if (type == null) return;

        // 1. Clean up attribute or other modifications mabe by the core
        CoreRegistry.get(type).ifPresent(core -> core.onRemoved(player));

        // 2. Wipe the slot track entry data reference
        if (isLeftSlot) {
            data.setLeftCore(null);
        } else {
            data.setRightCore(null);
        }
    }
}
