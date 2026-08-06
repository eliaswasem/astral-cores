package de.ep.astralrelics.manager;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import de.ep.astralrelics.relic.RelicRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PassiveAbilityManager {

    /* Iterates through online players and resolves cached database profiles via global main instance link */
    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            /* Fetching profile state from the global primary manager storage mapping */
            PlayerData data = AstralRelics.PLAYER_DATA.get(player);
            if (data == null) continue;

            /* Resolves and executes the left-hand slot passive effect if populated */
            if (data.getLeftRelic() != null) {
                RelicRegistry.get(data.getLeftRelic()).ifPresent(relic -> {
                    relic.applyPassive(player);
                });
            }

            /* Resolves and executes the right-hand slot passive effect if populated */
            if (data.getRightRelic() != null) {
                RelicRegistry.get(data.getRightRelic()).ifPresent(relic -> {
                    relic.applyPassive(player);
                });
            }
        }
    }
}
