package de.ep.astralcores.advancement.criterion.criterions;

import de.ep.astralcores.advancement.criterion.criterions.PigAltitudeCriterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CriterionRegistry {

    public static final PigAltitudeCriterion PIG_ALTITUDE =
            register("pig_altitude", new PigAltitudeCriterion());

    private static <T extends CriterionTrigger<?>> T register(
            final String name,
            final T criterion
    ) {
        return Registry.register(
                BuiltInRegistries.TRIGGER_TYPES,
                Identifier.fromNamespaceAndPath("astralcores", name),
                criterion
        );
    }
    public static void init() {
    }
}