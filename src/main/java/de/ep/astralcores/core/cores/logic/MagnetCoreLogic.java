package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.util.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class MagnetCoreLogic {

    private static final Set<ServerPlayer> activePlayers =
            Collections.newSetFromMap(new WeakHashMap<>());

    private MagnetCoreLogic() {
    }

    public static void applyPassive(ServerPlayer player) {
        activePlayers.add(player);
    }

    public static void onRemoved(ServerPlayer player) {
        activePlayers.remove(player);
    }

    public static void activate(ServerPlayer player) {
        // Magnetic Pull currently has no activation logic.
    }

    public static void executeMagneticDisarm(
            ServerPlayer attacker,
            ServerPlayer victim
    ) {
        // The attacker must currently have Magnet Core.
        if (!activePlayers.contains(attacker)) {
            return;
        }

        // Trusted players are not affected.
        var data = AstralCores.PLAYER_DATA.get(attacker);

        if (data != null && data.isTrusted(victim.getUUID())) {
            return;
        }

        // 50% trigger chance.
        if (attacker.getRandom().nextFloat() >= 0.50F) {
            return;
        }

        // Disable normal attacking for 30 ticks.
        Effects.applyEffect(
                victim,
                MobEffects.MINING_FATIGUE,
                30,
                255,
                false,
                false,
                false
        );

        victim.sendSystemMessage(
                Component.literal("Your Weapon is Magnetized")
                        .withStyle(
                                ChatFormatting.BOLD,
                                ChatFormatting.WHITE
                        )
        );
    }
}