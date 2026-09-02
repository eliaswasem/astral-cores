package de.ep.astralcores.advancement.criterion;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.criterions.VoidSurvivalCriterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CriterionRegistry {

    public static final VoidSurvivalCriterion VOID_SURVIVAL =
            register("void_survival", new VoidSurvivalCriterion());

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

    public static void init() {
        // Triggers are registered through the static fields above.
    }
}