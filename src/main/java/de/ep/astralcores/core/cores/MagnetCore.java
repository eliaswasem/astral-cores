package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;

public class MagnetCore extends Core {

    public MagnetCore() {
        super(
                CoreType.MAGNET_CORE,
                "§cMagnet Core",
                Items.IRON_INGOT,
                List.of(
                        "§4[Active: Magnetic Pull]"
                ),
                10012,
                0,
                0,
                "Magnetic Pull",
                "Magnetic Disarm",
                "\uE00C"
        );
    }

    @Override
    public void applyPassive(ServerPlayer victim) {
    if (Crit(victim)) {
        Effects.applyEffect(victim, MobEffects.MINING_FATIGUE, 10, 255, false, false, false);
        Effects.applyEffect(victim, MobEffects.WEAKNESS, 10, 255, false, false, false);
        }
    }

    @Override
    public void activate(ServerPlayer player) {

    }
    private boolean Crit(ServerPlayer player) {
        return !player.onGround()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.isSwimming()
                && player.isFallFlying();

    }
}
