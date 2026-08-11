package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class IllusionCore extends Core {

    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public IllusionCore() {
        super(
                CoreType.ILLUSION_CORE,
                "§fIllusion Core",
                Items.AMETHYST_SHARD,
                List.of(
                        "§f[Active: Mirror Swap]"
                ),
                10011,
                0,
                0,
                "Mirror Swap",
                "Mirror Image",
                "\uE00A"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {

        activePlayers.add(player);
    }
    @Override
    public void onRemoved(ServerPlayer player) {

        activePlayers.remove(player);
    }
    @Override
    public void activate(ServerPlayer player) {

    }
}
