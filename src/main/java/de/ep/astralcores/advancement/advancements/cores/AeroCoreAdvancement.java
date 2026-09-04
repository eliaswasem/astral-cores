package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.advancements.AdvancementHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class AeroCoreAdvancement {

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        skyPig(lookup, consumer);
    }

    private static void skyPig(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.WIND_CHARGE,
                        Component.literal("Sky Pig"),
                        Component.literal("Ride a pig above Y=4000."),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "pig_altitude",
                        PlayerTrigger.TriggerInstance.located(
                                EntityPredicate.Builder.entity()
                                        .vehicle(
                                                EntityPredicate.Builder.entity()
                                                        .of(
                                                                lookup.lookupOrThrow(
                                                                        Registries.ENTITY_TYPE
                                                                ),
                                                                EntityTypes.PIG
                                                        )
                                        )
                                        .located(
                                                LocationPredicate.Builder.location()
                                                        .setY(
                                                                MinMaxBounds.Doubles.atLeast(4000.0)
                                                        )
                                        )
                        )
                )
                .rewards(
                        AdvancementHelper.reward("aero_core")
                )
                .save(
                        consumer,
                        AdvancementHelper.advancementId("core/aero_core")
                );
    }
}
