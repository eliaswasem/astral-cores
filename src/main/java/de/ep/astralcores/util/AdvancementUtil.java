package de.ep.astralcores.util;

import de.ep.astralcores.AstralCores;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public final class AdvancementUtil {

    public static Identifier advancementId(String name) {
        return Identifier.fromNamespaceAndPath(
                AstralCores.MOD_ID,
                name
        );
    }

    public static Criterion<?> hasItem(
            HolderLookup.Provider lookup,
            Item item
    ) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item()
                        .of(
                                lookup.lookupOrThrow(Registries.ITEM),
                                item
                        )
        );
    }

    public static Criterion<?> isInBiome(
            HolderLookup.Provider lookup,
            ResourceKey<Biome> biome
    ) {
        Holder<Biome> holder = lookup
                .lookupOrThrow(Registries.BIOME)
                .getOrThrow(biome);

        return PlayerTrigger.TriggerInstance.located(
                LocationPredicate.Builder.location()
                        .setBiomes(HolderSet.direct(holder))
        );
    }

    public static AdvancementRewards.Builder reward(String core) {
        return new AdvancementRewards.Builder()
                .runs(
                        Identifier.fromNamespaceAndPath(
                                AstralCores.MOD_ID,
                                "core/" + core
                        )
                );
    }
}