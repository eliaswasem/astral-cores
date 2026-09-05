package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ChronoCoreAdvancement {

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        timerTraveler(lookup, consumer);
    }

    private static void timerTraveler(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.WIND_CHARGE,
                        Component.literal("Time Traveler"),
                        Component.literal("Ride a pig above Y=4000."),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "horse",
                        PlayerTrigger.TriggerInstance.located(
                                EntityPredicate.Builder.entity()
                                        .vehicle(
                                                EntityPredicate.Builder.entity()
                                                        .of(
                                                                lookup.lookupOrThrow(
                                                                        Registries.ENTITY_TYPE
                                                                ),
                                                                EntityTypes.HORSE
                                                        )
                                        )
                        )
                )

                .addCriterion(
                        "skeleton_horse",
                        PlayerTrigger.TriggerInstance.located(
                                EntityPredicate.Builder.entity()
                                        .vehicle(
                                                EntityPredicate.Builder.entity()
                                                        .of(
                                                                lookup.lookupOrThrow(
                                                                        Registries.ENTITY_TYPE
                                                                ),
                                                                EntityTypes.SKELETON_HORSE
                                                        )
                                        )
                        )
                )

                .addCriterion(
                        "zombie_horse",
                        PlayerTrigger.TriggerInstance.located(
                                EntityPredicate.Builder.entity()
                                        .vehicle(
                                                EntityPredicate.Builder.entity()
                                                        .of(
                                                                lookup.lookupOrThrow(
                                                                        Registries.ENTITY_TYPE
                                                                ),
                                                                EntityTypes.ZOMBIE_HORSE
                                                        )
                                        )
                        )
                )

                .rewards(
                        AdvancementUtil.reward("chrono_core")
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/chrono_core")
                );
    }
}
