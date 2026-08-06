package de.ep.astralrelics;

import de.ep.astralrelics.manager.PassiveAbilityManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.jspecify.annotations.NonNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
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

                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        player.connection.send(
                                new ClientboundSetActionBarTextPacket(
                                        Component.literal("\uE000")
                                )
                        );
                    }

                    /* Executed once every 20 server ticks or one second */
                    twentyTickLoop(server);
                }
            }
        });
    }

    /* Isolated empty placeholder loop for high-frequency operations */
    private static void oneTickLoop(MinecraftServer server) {

    }

    /* Isolated empty placeholder loop for low-frequency operations */
    private static void twentyTickLoop(MinecraftServer server) {
        PassiveAbilityManager.tick(server);
    }
}
