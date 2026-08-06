package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;

import java.util.List;

public class PhoenixCore extends Relic {

    public PhoenixCore() {
        super(
                RelicType.PHOENIX_CORE,
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
        player.addEffect(
                new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE,
                        20,
                        0,
                        false,
                        false,
                        true
                )
        );
    }


    /*
     * Area-of-effect fire ignition ability deployment.
     */
    @Override
    public void activate(ServerPlayer player) {

        player.level()
                .getEntities(
                        player,
                        player.getBoundingBox().inflate(5.0),
                        Entity::isAlive
                )
                .forEach(entity -> {
                    entity.setRemainingFireTicks(100);
                });


        player.sendSystemMessage(
                Component.literal(
                        "§cYou unleashed a Fire Burst!"
                )
        );
    }
}