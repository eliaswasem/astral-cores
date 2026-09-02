package de.ep.astralcores.datagen;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.Biomes;
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


        Advancement.Builder.advancement()
                .display(
                        Blocks.OAK_LEAVES,
                        Component.literal("Farming Dedication"),
                        Component.literal(
                                "Visit a forest, jungle, taiga, swamp, mangrove swamp and savanna, and obtain a Netherite Hoe with Mending and Unbreaking."
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                // Forest
                .addCriterion(
                        "forest",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                lookup.lookupOrThrow(Registries.BIOME)
                                                        .getOrThrow(Biomes.FOREST)
                                                )
                                        )
                        )
                )

                // Jungle
                .addCriterion(
                        "jungle",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                lookup.lookupOrThrow(Registries.BIOME)
                                                        .getOrThrow(Biomes.JUNGLE)
                                                )
                                        )
                        )
                )

                // Taiga
                .addCriterion(
                        "taiga",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                lookup.lookupOrThrow(Registries.BIOME)
                                                        .getOrThrow(Biomes.TAIGA)
                                                )
                                        )
                        )
                )

                // Swamp
                .addCriterion(
                        "swamp",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.SWAMP)
                                                )
                                        )
                        )
                )

                // Mangrove Swamp
                .addCriterion(
                        "mangrove_swamp",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                HolderSet.direct(
                                                        lookup.lookupOrThrow(Registries.BIOME)
                                                                .getOrThrow(Biomes.MANGROVE_SWAMP)
                                                )
                                        )
                        )
                )

                // Savanna
                .addCriterion(
                        "savanna",
                        PlayerTrigger.TriggerInstance.located(
                                LocationPredicate.Builder.location()
                                        .setBiomes(
                                                lookup.lookupOrThrow(Registries.BIOME)
                                                        .getOrThrow(BiomeTags.IS_SAVANNA)
                                        )
                        )
                )

                // Netherite Hoe with Mending + Unbreaking
                .addCriterion(
                        "enchanted_netherite_hoe",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item()
                                        .of(
                                                lookup.lookupOrThrow(Registries.ITEM),
                                                Items.NETHERITE_HOE
                                        )
                                        .withComponents(
                                                DataComponentMatchers.Builder
                                                        .components()
                                                        .partial(
                                                                DataComponentPredicates.ENCHANTMENTS,
                                                                EnchantmentsPredicate.enchantments(
                                                                        List.of(
                                                                                new EnchantmentPredicate(
                                                                                        lookup.lookupOrThrow(
                                                                                                Registries.ENCHANTMENT
                                                                                        ).getOrThrow(
                                                                                                Enchantments.MENDING
                                                                                        ),
                                                                                        MinMaxBounds.Ints.atLeast(1)
                                                                                ),
                                                                                new EnchantmentPredicate(
                                                                                        lookup.lookupOrThrow(
                                                                                                Registries.ENCHANTMENT
                                                                                        ).getOrThrow(
                                                                                                Enchantments.UNBREAKING
                                                                                        ),
                                                                                        MinMaxBounds.Ints.atLeast(1)
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .build()
                                        )
                        )
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
    }
    }