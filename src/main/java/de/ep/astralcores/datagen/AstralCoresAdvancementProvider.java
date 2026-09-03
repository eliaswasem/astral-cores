package de.ep.astralcores.datagen;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.EffectsChangedTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
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
                        new AdvancementRewards.Builder()
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


       /* Advancement.Builder.advancement()
                .display(
                        Blocks.OAK_LEAVES,
                        Component.literal("Best Botanic"),
                        Component.literal(
                                "Collect every flower and every sapling from the Minecraft Overworld"
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                // ALL criteria are required
                .requirements(
                        AdvancementRequirements.Strategy.AND
                )

                // Reward
                .rewards(
                        new AdvancementRewards.Builder()
                                .runs(
                                        Identifier.fromNamespaceAndPath(
                                                AstralCores.MOD_ID,
                                                "cores/nature_core"
                                        )
                                )
                )

                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/nature_core"
                        )
                );

        */

        Advancement.Builder.advancement()
                .display(
                        Blocks.CONDUIT,
                        Component.literal("What a breath"),
                        Component.literal("Have Conduit Power 2, Water breathing & Dolphins Grace"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                .addCriterion(
                        "conduit_power_2",
                        EffectsChangedTrigger.TriggerInstance.hasEffects(
                                MobEffectsPredicate.Builder.effects()
                                        .and(
                                                MobEffects.CONDUIT_POWER,
                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                        MinMaxBounds.Ints.exactly(1),
                                                        MinMaxBounds.Ints.exactly(1),
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        )
                        )
                )

                .addCriterion(
                        "water_breathing",
                        EffectsChangedTrigger.TriggerInstance.hasEffects(
                                MobEffectsPredicate.Builder.effects()
                                        .and(
                                                MobEffects.WATER_BREATHING,
                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                        MinMaxBounds.Ints.ANY,
                                                        MinMaxBounds.Ints.ANY,
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        )
                        )

                )

                .addCriterion(
                        "dolphins_grace",
                        EffectsChangedTrigger.TriggerInstance.hasEffects(
                                MobEffectsPredicate.Builder.effects()
                                        .and(
                                                MobEffects.DOLPHINS_GRACE,
                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                        MinMaxBounds.Ints.ANY,
                                                        MinMaxBounds.Ints.ANY,
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        )
                        )
                )

                .requirements(
                        AdvancementRequirements.Strategy.AND
                )

                .rewards(
                        new AdvancementRewards.Builder()
                                .runs(
                                        Identifier.fromNamespaceAndPath(
                                                AstralCores.MOD_ID,
                                                "cores/leviathan_core"
                                        )
                                )
                )

                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/leviathan_core"
                        )
                );

        Advancement.Builder.advancement()
                .display(
                        Items.BREEZE_ROD,
                        Component.literal("It gotta go FAST!!!"),
                        Component.literal("Have Speed 2, Dolphins Grace, Netherite Boots with soul speed 3 while walking on soul sand"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                .addCriterion(
                        "critereon_gale_core",
                        PlayerTrigger.TriggerInstance.located(
                                EntityPredicate.Builder.entity()
                                        .steppingOn(
                                                LocationPredicate.Builder.location()
                                                        .setBlock(
                                                                BlockPredicate.Builder.block()
                                                                        .of(
                                                                                lookup.lookupOrThrow(Registries.BLOCK),
                                                                                Blocks.SOUL_SAND
                                                                        )
                                                        )
                                        )

                                        .equipment(
                                                EntityEquipmentPredicate.Builder.equipment()
                                                        .feet(
                                                                ItemPredicate.Builder.item()
                                                                        .of(
                                                                                lookup.lookupOrThrow(Registries.ITEM),
                                                                                Items.NETHERITE_BOOTS
                                                                        )
                                                                        .withComponents(
                                                                                DataComponentMatchers.Builder.components()
                                                                                        .partial(
                                                                                                DataComponentPredicates.ENCHANTMENTS,
                                                                                                EnchantmentsPredicate.enchantments(
                                                                                                        List.of(
                                                                                                                new EnchantmentPredicate(
                                                                                                                        lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED),
                                                                                                                        MinMaxBounds.Ints.atLeast(3)
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                        .build()
                                                                        )
                                                        )
                                        )

                                        .effects(
                                                MobEffectsPredicate.Builder.effects()
                                                        .and(
                                                                MobEffects.SPEED,
                                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                                        MinMaxBounds.Ints.atLeast(1),
                                                                        MinMaxBounds.Ints.atLeast(1),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                        .and(
                                                                MobEffects.DOLPHINS_GRACE,
                                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                                        MinMaxBounds.Ints.ANY,
                                                                        MinMaxBounds.Ints.ANY,
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                        )
                        )
                )

                .requirements(
                        AdvancementRequirements.Strategy.AND
                )

                .rewards(
                        new AdvancementRewards.Builder()
                                .runs(
                                        Identifier.fromNamespaceAndPath(
                                                AstralCores.MOD_ID,
                                                "cores/gale_core"
                                        )
                                )
                )

                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/gale_core"
                        )
                );


    }
    }