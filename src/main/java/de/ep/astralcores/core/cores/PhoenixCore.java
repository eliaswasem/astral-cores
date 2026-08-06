package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;

public class PhoenixCore extends Core {

    public PhoenixCore() {
        super(
                CoreType.PHOENIX_CORE,
                "§cPhoenix Core",
                Items.MAGMA_CREAM,
                List.of(
                        "§7Forged in cosmic radiation.",
                        "§6[Active: Fire Burst]"
                ),
                10003,
                30,
                0
        );
    }


    /*
     * Continuous passive fire immunity application.
     */
    @Override
    public void applyPassive(ServerPlayer player) {
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 20, 1);
    }


    /*
     * Area-of-effect fire ignition ability deployment.
     */
    @Override
    public void activate(ServerPlayer player) {

        Effects.applyEffect(player, MobEffects.BLINDNESS, 20, 1);


        player.sendSystemMessage(
                Component.literal(
                        "§cYou unleashed a Fire Burst!"
                )
        );
    }
}