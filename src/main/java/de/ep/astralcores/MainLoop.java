package de.ep.astralcores;

import de.ep.astralcores.core.respawn.CoreRespawnManager;
import de.ep.astralcores.core.respawn.data.CoreRespawnData;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.manager.CoreTickManager;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.manager.CriterionTickManager;
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

                oneTickLoop(server);

                this.ticks++;
                if (this.ticks >= 20) {
                    this.ticks = 0;

                    twentyTickLoop(server);
                }
            }
        });
    }

    // Loop that runs every servertick
    private static void oneTickLoop(MinecraftServer server) {

        CoreRespawnManager.tick();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            CriterionTickManager.tick(player);

            PlayerData data = AstralCores.PLAYER_DATA.get(player);
            // Applies the cores tick function
            CoreTickManager.tick(player, data);
        }
    }

    // Loop that runs every second
    private static void twentyTickLoop(MinecraftServer server) {
        // Runs for every online player
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerData data = AstralCores.PLAYER_DATA.get(player);

            // Updates and manages the Cooldowns every second
            CooldownManager.tick(player, data);
            // Updates and manages the custom ActionBar HUD every second
            ActionBarManager.tick(player, data);
            // Applies the cores passive Ability function
            CoreTickManager.tickPassiveAbility(player, data);
        }


    }
}
