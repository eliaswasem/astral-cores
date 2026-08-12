package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.MagnetCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;



public class MagnetCorePassiveLogic {
    public static boolean handleMagneticDisarm(ServerPlayer player, DamageSource source) {

        if (!MagnetCore.activePlayers.contains(player)) {
            return true;
        }

            return false;
    }
}