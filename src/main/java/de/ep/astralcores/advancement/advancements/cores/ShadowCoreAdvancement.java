package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Consumer;

public class ShadowCoreAdvancement {

    private ShadowCoreAdvancement() {
    }

    public static void generate(

            Consumer<AdvancementHolder> consumer

    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.ENDER_EYE,
                        Component.literal("Where did i go"),
                        Component.literal(
                                "Disappear into the void and survive it."
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "void_survival",
                        CriterionRegistry.VOID_SURVIVAL.createCriterion(
                                new VoidSurvivalCriterion.Conditions(
                                        Optional.empty()
                                )
                        )
                )
                .rewards(
                        AdvancementUtil.reward("shadow_core")
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/shadow_core")
                );
    }
}
