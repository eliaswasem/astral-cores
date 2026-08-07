package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;

public class GaleCore extends Core {

    public GaleCore() {
        super(
                CoreType.GALE_CORE,
                "§7Gale Core",
                Items.BREEZE_ROD,
                List.of(
                        "§f[Active: Sonic Dash]"
                ),
                10005,
                0,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        if (player.isSprinting()) {
            Effects.applyEffect(player, MobEffects.SPEED, 25, 1);
        }

    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
