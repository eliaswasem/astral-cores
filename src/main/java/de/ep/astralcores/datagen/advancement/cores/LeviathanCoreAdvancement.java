package de.ep.astralcores.datagen.advancement.cores;

import de.ep.astralcores.datagen.advancement.AdvancementHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.triggers.EffectsChangedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Consumer;

public class LeviathanCoreAdvancement {

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        whatABreath(lookup, consumer);
    }

    private static void whatABreath(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Blocks.CONDUIT,
                        Component.literal("What a breath"),
                        Component.literal("Have Conduit Power, Water breathing & Dolphins Grace"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )

                .addCriterion(
                        "all_effects",
                        EffectsChangedTrigger.TriggerInstance.hasEffects(
                                MobEffectsPredicate.Builder.effects()
                                        .and(
                                                MobEffects.CONDUIT_POWER,
                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                        MinMaxBounds.Ints.ANY,
                                                        MinMaxBounds.Ints.ANY,
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        )
                                        .and(
                                                MobEffects.WATER_BREATHING,
                                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                                        MinMaxBounds.Ints.ANY,
                                                        MinMaxBounds.Ints.ANY,
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

                .rewards(
                        AdvancementHelper.reward("leviathan_core")
                )
                .save(
                        consumer,
                        AdvancementHelper.advancementId("core/leviathan_core")
                );
    }
}
