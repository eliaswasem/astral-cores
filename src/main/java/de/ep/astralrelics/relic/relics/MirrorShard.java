package de.ep.astralrelics.relic.relics;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

import java.util.List;

public class MirrorShard extends Relic {

    public MirrorShard() {
        super(
                RelicType.MIRROR_RELICT,
                "§fMirror Shard",
                Items.AMETHYST_SHARD,
                List.of(
                        "§f[Active: Mirror Swap]"
                ),
                10011,
                0,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {

    }

    @Override
    public void activate(ServerPlayer player) {

    }
}
