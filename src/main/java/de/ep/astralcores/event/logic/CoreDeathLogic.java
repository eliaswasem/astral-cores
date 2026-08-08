package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.manager.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;

public class CoreDeathLogic {

    // Process core removal on player death if enabled in the config
    public static void executeDeathDrop(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) return;

        // Check if dropping cores on death is disabled
        if (!ConfigManager.get().general.drop_core_on_death) {
            return;
        }

        // Process the single equipped core slot
        CoreType equipped = data.getEquippedCore();
        if (equipped != null) {
            // Clean up attributes or passive buffers before removal
            CoreRegistry.get(equipped).ifPresent(core -> core.onRemoved(player));

            // Wipe the core data from the player slot
            data.setEquippedCore(null);
            // Instantly update Actionbar
            ActionBarManager.tick(player, data);
        }
    }
}
