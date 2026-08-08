package de.ep.astralcores.manager;

import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.server.level.ServerPlayer;

public class PassiveAbilityManager {

    /**
     * Resolves and executes the single equipped core passive effect if populated.
     * Invoked sequentially within the centralized server main loop loop handler.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param data   The cached PlayerData reference provided by the central main loop.
     */
    public static void tick(ServerPlayer player, PlayerData data) {
        if (data == null) return;

        /* Resolves and executes the single equipped core passive effect if populated */
        if (data.getEquippedCore() != null) {
            CoreRegistry.get(data.getEquippedCore()).ifPresent(core -> {
                core.applyPassive(player);
            });
        }
    }
}
