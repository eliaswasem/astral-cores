package de.ep.astralcores.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CoreFactory {

    // Creates an item stack configured with the properties of the given core
    public static ItemStack createStack(Core core) {

        ItemStack stack = new ItemStack(
                core.getBaseItem()
        );

        // Sets the custom display name for the core item
        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(
                        core.getName()
                )
        );

        // Converts and applies the text lines to the item lore
        if (core.getLore() != null && !core.getLore().isEmpty()) {

            stack.set(
                    DataComponents.LORE,
                    new ItemLore(
                            core.getLore()
                                    .stream()
                                    .map(Component::literal)
                                    .collect(Collectors.toList())
                    )
            );
        }

        // Applies custom model data to enable custom resource pack textures
        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.of((float) core.getCustomModelData()),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        // Forces the maximum stack size of the core item to one
        stack.set(DataComponents.MAX_STACK_SIZE, 1);

        // Fetches or creates the custom data nbt compound tag for the item
        CompoundTag tag = stack
                .getOrDefault(
                        DataComponents.CUSTOM_DATA,
                        CustomData.EMPTY
                )
                .copyTag();

        // Writes the custom core identifier string into the nbt tag
        tag.putString(
                "core_id",
                core.getCoreId()
        );

        // Commits the modified custom nbt data back to the item stack
        stack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
        );

        return stack;
    }

    // Resolves a matching core instance from the custom data tags of an item stack
    public static Optional<Core> getCoreFromItem(ItemStack stack) {

        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }

        CompoundTag tag = Objects.requireNonNull(
                stack.get(DataComponents.CUSTOM_DATA)
        ).copyTag();

        // Reads the core identifier string from the item nbt data
        Optional<String> coreId = tag.getString(
                "core_id"
        );

        // Looks up the core type inside the central registry using the found id string
        return coreId.flatMap(CoreRegistry::getByCoreId);
    }
}
