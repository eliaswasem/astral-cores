package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class MagnetCore extends Core {

    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

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
    public void applyPassive(ServerPlayer player) {

        activePlayers.add(player);
    }

    @Override
    public void activate(ServerPlayer player) {

    }

    @Override
    public void onRemoved(ServerPlayer player) {

        activePlayers.remove(player);
    }
}
