package de.ep.astralcores.datagen;

import de.ep.astralcores.advancement.advancements.cores.*;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;

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
        AeroCoreAdvancement.generate(lookup, consumer);
        ShadowCoreAdvancement.generate(consumer);
        NatureCoreAdvancement.generate(lookup, consumer);
        ChronoCoreAdvancement.generate(lookup, consumer);
        LeviathanCoreAdvancement.generate(lookup, consumer);
        GaleCoreAdvancement.generate(lookup, consumer);

    }
}
