package de.ep.astralrelics.events;

import de.ep.astralrelics.AstralRelics;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class PlayerEvents {


    public static void register() {


        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> {

                    AstralRelics.PLAYER_DATA.load(
                            handler.player
                    );

                }
        );


        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> {

                    AstralRelics.PLAYER_DATA.unload(
                            handler.player
                    );

                }
        );


    }

}