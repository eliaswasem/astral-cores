package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.WeakHashMap;

public class BerserkerCore extends Core {

    // Track which players currently have this core's passive effect active
    public static final WeakHashMap<ServerPlayer, Boolean> activePlayers = new WeakHashMap<>();

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
                "Rage Mode",
                "Bloodlust",
                "\uE009"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        if(player.getHealth() <= 7){
            Effects.applyEffect(player, MobEffects.STRENGTH, 25, 3);
        }

        // Mark this player as having the core active
        activePlayers.put(player, true);
    }

    @Override
    public void activate(ServerPlayer player) {
    }
}