package de.ep.astralcores.advancement.criterion;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.criterions.NetherTimeCriterion;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriterionRegistry {

    public static final VoidSurvivalCriterion VOID_SURVIVAL =
            register("void_survival", new VoidSurvivalCriterion());

    public static final NetherTimeCriterion NETHER_TIME =
            register("nether_time", new NetherTimeCriterion());

    private static <T extends CriterionTrigger<?>> T register(
            String name,
            T criterion
    ) {
        return Registry.register(
                BuiltInRegistries.TRIGGER_TYPES,
                Identifier.fromNamespaceAndPath(
                        AstralCores.MOD_ID,
                        name
                ),
                criterion
        );
    }

    private static final Map<UUID, Long> NETHER_TICKS = new HashMap<>();

    public static void init() {
        // Triggers are registered through the static fields above.


            // triggers are registered through the static fields above.

            ServerTickEvents.END_SERVER_TICK.register(server -> {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    UUID uuid = player.getUUID();

                    if (player.level().dimension() == net.minecraft.world.level.Level.NETHER) {
                        long ticks = NETHER_TICKS.merge(uuid, 1L, Long::sum);

                        NETHER_TIME.trigger(player, ticks);
                    } else {
                        NETHER_TICKS.remove(uuid);
                    }
                }
            });
    }
}