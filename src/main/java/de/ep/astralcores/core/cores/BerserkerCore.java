package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.BerserkerCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class BerserkerCore extends Core {

    public BerserkerCore() {
        super(
                CoreType.BERSERKER_CORE,
                "§6Berseker Core",
                Items.BLAZE_POWDER,
                List.of(
                        "§6[Active: Rage Mode]"
                ),
                10011,
                0,
                0,
                86400L,
                "Rage Mode",
                "Bloodlust",
                "\uE009",
                BossEvent.BossBarColor.RED
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        BerserkerCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        BerserkerCoreLogic.activate(player);
    }

    @Override
    public void tick() {
        BerserkerCoreLogic.tick();
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        BerserkerCoreLogic.onRemoved(player);
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        BerserkerCoreLogic.onPlayerDisconnect(player);
    }
}
