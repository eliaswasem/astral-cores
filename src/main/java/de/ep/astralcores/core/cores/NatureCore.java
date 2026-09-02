package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.NatureCoreLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Items;

import java.util.List;

public class NatureCore extends Core {

    public NatureCore() {
        super(
                CoreType.NATURE_CORE,
                "§2Nature Core",
                Items.SLIME_BALL,
                List.of(
                        "§2[Active: Root Trap]"
                ),
                10012,
                0,
                0,
                86400L,
                "Root Trap",
                "Nature Blessing",
                "\uE00B",
                BossEvent.BossBarColor.GREEN
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        NatureCoreLogic.applyPassive(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        NatureCoreLogic.activate(player);
    }
}