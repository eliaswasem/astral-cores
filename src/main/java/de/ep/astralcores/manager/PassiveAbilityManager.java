package de.ep.astralcores.manager;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PassiveAbilityManager {

    /* Iterates through online players and resolves cached database profiles via global main instance link */
    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            /* Fetching profile state from the global primary manager storage mapping */
            PlayerData data = AstralCores.PLAYER_DATA.get(player);
            if (data == null) continue;

            /* Resolves and executes the left-hand slot passive effect if populated */
            if (data.getLeftCore() != null) {
                CoreRegistry.get(data.getLeftCore()).ifPresent(relic -> {
                    relic.applyPassive(player);
                });
            }

            /* Resolves and executes the right-hand slot passive effect if populated */
            if (data.getRightCore() != null) {
                CoreRegistry.get(data.getRightCore()).ifPresent(core -> {
                    core.applyPassive(player);
                });
            }
        }
    }
}
