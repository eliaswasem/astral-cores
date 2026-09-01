package de.ep.astralcores.datagen;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.advancement.criterion.criterions.PigAltitudeCriterion;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
            HolderLookup.Provider wrapperLookup,
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
                        CriterionRegistry.PIG_ALTITUDE.createCriterion(
                                new PigAltitudeCriterion.Conditions(Optional.empty())
                        )
                )
                .rewards(
                        new net.minecraft.advancements.AdvancementRewards.Builder()
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
    }
}