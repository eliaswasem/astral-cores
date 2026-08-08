package de.ep.astralcores;

import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.manager.PassiveAbilityManager;
import de.ep.astralcores.manager.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jspecify.annotations.NonNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class MainLoop {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new ServerTickEvents.EndTick() {
            private int ticks = 0;

            @Override
            public void onEndTick(@NonNull MinecraftServer server) {

                /* Executed every single server tick */
                oneTickLoop(server);

                this.ticks++;
                if (this.ticks >= 20) {
                    this.ticks = 0;
                    /* Executed once every 20 server ticks or one second */
                    twentyTickLoop(server);
                }
            }
        });
    }

    /* Isolated empty placeholder loop for high-frequency operations */
    private static void oneTickLoop(MinecraftServer server) {

    }

    /* Centralized low-frequency operations loop running once every second */
    private static void twentyTickLoop(MinecraftServer server) {
        // Enforce a single high-performance loop architecture over all online profiles
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerData data = AstralCores.PLAYER_DATA.get(player);

            // Sequential delegate execution leveraging cached data addresses
            CooldownManager.tick(player, data);
            PassiveAbilityManager.tick(player, data);
            ActionBarManager.tick(player, data);
        }
    }
}
