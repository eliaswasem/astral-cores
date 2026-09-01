package de.ep.astralcores.manager;

import de.ep.astralcores.advancement.criterion.criterions.CriterionRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class CriterionTickManager {


    public static void tick(ServerPlayer player) {
        CriterionRegistry.PIG_ALTITUDE.trigger(player);
    }
}