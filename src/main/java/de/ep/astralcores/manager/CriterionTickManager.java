package de.ep.astralcores.manager;

import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class CriterionTickManager {


    public static void tick(ServerPlayer player) {
        CriterionRegistry.VOID_SURVIVAL.trigger(player);
    }
}