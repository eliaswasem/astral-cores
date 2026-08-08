package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.manager.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;

public class CoreDeathLogic {

    // Handles core unequipment and drops when a player dies
    public static void executeDeathDrop(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) return;

        // Stops execution if dropping cores on death is disabled in the config
        if (!ConfigManager.get().general.drop_core_on_death) {
            return;
        }

        CoreType equipped = data.getEquippedCore();
        if (equipped != null) {
            // Runs cleanup actions for the equipped core before removal
            CoreRegistry.get(equipped).ifPresent(core -> core.onRemoved(player));

            // Clears the core type from the player data slot
            data.setEquippedCore(null);

            // Updates the action bar display text immediately
            ActionBarManager.tick(player, data);
        }
    }
}
