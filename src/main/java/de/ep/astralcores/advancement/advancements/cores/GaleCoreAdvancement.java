package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GaleCoreAdvancement {
    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        itNeedsToBeFast(lookup, consumer);
    }

    private static void itNeedsToBeFast(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.BREEZE_ROD,
                        Component.literal("It needs to be FAST!!!"),
                        Component.literal(
                                "Have Speed 2, Dolphins Grace and Netherite Boots with Soul Speed 3 while walking on soul sand"
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                .addCriterion(
                        "criterion_gale_core",
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
                                                                                                                        lookup.lookupOrThrow(
                                                                                                                                Registries.ENCHANTMENT
                                                                                                                        ).getOrThrow(
                                                                                                                                Enchantments.SOUL_SPEED
                                                                                                                        ),
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
                                                                new MobEffectsPredicate.MobEffectInstancePredicate()
                                                        )
                                        )
                        )
                )
                    .rewards(
                            AdvancementUtil.reward("gale_core")
                    )
                    .save(
                            consumer,
                            AdvancementUtil.advancementId("core/gale_core")
                    );
    }
}
