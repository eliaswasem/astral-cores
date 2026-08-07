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

            /* Resolves and executes the single equipped core passive effect if populated */
            if (data.getEquippedCore() != null) {
                CoreRegistry.get(data.getEquippedCore()).ifPresent(core -> {
                    core.applyPassive(player);
                });
            }
        }
    }
}
