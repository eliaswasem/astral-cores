package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.advancements.AdvancementHelper;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.NetherTimeCriterion;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;


import java.awt.*;
import java.util.function.Consumer;

public class PhoenixCoreAdvancement {

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        warmLiving(lookup, consumer);
    }

    private static void warmLiving(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.WIND_CHARGE,
                        Component.literal("Warm Living"),
                        Component.literal(
                                "Be for 12 hours continuously in the nether and were in every nether biome"
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                .addCriterion(
                        "nether_wastes",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.NETHER_WASTES)
                                                )
                                        )
                        )
                )

                .addCriterion(
                        "crimson_forest",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.CRIMSON_FOREST)
                                                )
                                        )
                        )
                )

                .addCriterion(
                        "warped_forest", //tarangelus wald
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.WARPED_FOREST)
                                                )
                                        )
                        )
                )

                .addCriterion(
                        "soul_sand_valley",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.SOUL_SAND_VALLEY)
                                                )
                                        )
                        )
                )

                .addCriterion(
                        "basalt_deltas",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.BASALT_DELTAS)
                                                )
                                        )
                        )
                )

                .addCriterion("stayed_in_nether",

                        CriterionRegistry.NETHER_TIME.createCriterion(

                                new NetherTimeCriterion.Conditions(java.util.Optional.empty(), 1200L)
                        )
                )
                .rewards(
                        AdvancementHelper.reward("phoenix_core")
                )
                .save(
                        consumer,
                        AdvancementHelper.advancementId("core/phoenix_core")
                );



    }
}
