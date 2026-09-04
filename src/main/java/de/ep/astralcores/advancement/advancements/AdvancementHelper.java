package de.ep.astralcores.advancement.advancements;

import de.ep.astralcores.AstralCores;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public final class AdvancementHelper {

    private AdvancementHelper() {
    }

    public static Identifier advancementId(String name) {
        return Identifier.fromNamespaceAndPath(
                AstralCores.MOD_ID,
                "core/" + name
        );
    }

    public static Identifier coreReward(String name) {
        return Identifier.fromNamespaceAndPath(
                AstralCores.MOD_ID,
                "cores/" + name
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

    public static AdvancementRewards.Builder reward(String name) {
        return new AdvancementRewards.Builder()
                .runs(coreReward(name));
    }
}