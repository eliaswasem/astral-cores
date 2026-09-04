package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.advancements.AdvancementHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

public class NatureCoreAdvancement {

    private static final List<Item> FLOWERS = List.of(
            Items.ALLIUM,
            Items.AZURE_BLUET,
            Items.BLUE_ORCHID,
            Items.CORNFLOWER,
            Items.DANDELION,
            Items.OPEN_EYEBLOSSOM,
            Items.CLOSED_EYEBLOSSOM,
            Items.LILY_OF_THE_VALLEY,
            Items.OXEYE_DAISY,
            Items.POPPY,
            Items.ROSE_BUSH,
            Items.SPORE_BLOSSOM,
            Items.SUNFLOWER,
            Items.TORCHFLOWER,
            Items.RED_TULIP,
            Items.ORANGE_TULIP,
            Items.PINK_TULIP,
            Items.WHITE_TULIP
    );

    private static final List<Item> SAPLINGS = List.of(
            Items.ACACIA_SAPLING,
            Items.AZALEA,
            Items.BIRCH_SAPLING,
            Items.CHERRY_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.MANGROVE_PROPAGULE,
            Items.OAK_SAPLING,
            Items.PALE_OAK_SAPLING,
            Items.SPRUCE_SAPLING
    );

    public static void generate(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        bestBotanic(lookup, consumer);
    }

    private static void bestBotanic(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(
                        Blocks.OAK_LEAVES,
                        Component.literal("Best Botanic"),
                        Component.literal(
                                "Collect every flower and sapling from the Overworld"
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                );

        for (Item item : FLOWERS) {
            addItemCriterion(builder, lookup, item);
        }

        for (Item item : SAPLINGS) {
            addItemCriterion(builder, lookup, item);
        }

        builder
                .rewards(
                        AdvancementHelper.reward("nature_core")
                )
                .save(
                        consumer,
                        AdvancementHelper.advancementId("core/nature_core")
                );
    }

    private static void addItemCriterion(
            Advancement.Builder builder,
            HolderLookup.Provider lookup,
            Item item
    ) {
        builder.addCriterion(
                BuiltInRegistries.ITEM
                        .getKey(item)
                        .getPath(),
                AdvancementHelper.hasItem(lookup, item)
        );
    }
}
