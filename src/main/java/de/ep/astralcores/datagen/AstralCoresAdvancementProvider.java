package de.ep.astralcores.datagen;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;


import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class AstralCoresAdvancementProvider extends FabricAdvancementProvider {

    protected AstralCoresAdvancementProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {

        // Generate Sky Pig advancement for Aero Core
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
                                                                lookup.lookupOrThrow(Registries.ENTITY_TYPE),
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
                        new AdvancementRewards.Builder()
                                .runs(
                                        Identifier.fromNamespaceAndPath(
                                                AstralCores.MOD_ID,
                                                "cores/aero_core"
                                        )
                                )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/aero_core"
                        )
                );


        Advancement.Builder.advancement()
                .display(
                        Items.ENDER_EYE,
                        Component.literal("Where did i go"),
                        Component.literal("Disappear into the void and survive it."),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "void_survival",
                        CriterionRegistry.VOID_SURVIVAL.createCriterion(
                                new VoidSurvivalCriterion.Conditions(Optional.empty())
                        )
                )
                .rewards(
                        new net.minecraft.advancements.AdvancementRewards.Builder()
                                .runs(
                                        Identifier.fromNamespaceAndPath(
                                                AstralCores.MOD_ID,
                                                "cores/shadow_core"
                                        )
                                )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/shadow_core"
                        )
                );
    }
}
