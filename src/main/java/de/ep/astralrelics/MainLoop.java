package de.ep.astralrelics;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.jspecify.annotations.NonNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class MainLoop {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new ServerTickEvents.EndTick() {
            private int ticks = 0;

            @Override
            public void onEndTick(net.minecraft.server.@NonNull MinecraftServer server) {
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


                        //CooldownManager.tick(server);
                    //PassiveRelicManager.tick(server);
                }
            }
        });
    }
}
