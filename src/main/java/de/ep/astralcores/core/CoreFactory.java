package de.ep.astralcores.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CoreFactory {

        // Creates an ItemStack configured with the properties of the given core
        public static CoreStackResult createStack(Core core) {

            // Creates the ItemStack using the base item of the core
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
            stack.set(
                    DataComponents.MAX_STACK_SIZE,
                    1
            );

            // Creates the unique UUID for the core
            UUID coreUuid = UUID.randomUUID();

            // Fetches or creates the custom data NBT compound tag for the item
            CompoundTag tag = stack
                    .getOrDefault(
                            DataComponents.CUSTOM_DATA,
                            CustomData.EMPTY
                    )
                    .copyTag();

            // Writes the custom core identifier string into the NBT tag
            tag.putString(
                    "core_id",
                    core.getCoreId()
            );

            // Writes the unique core UUID into the NBT tag
            tag.putString(
                    "core_uuid",
                    coreUuid.toString()
            );

            // Commits the modified custom NBT data back to the ItemStack
            stack.set(
                    DataComponents.CUSTOM_DATA,
                    CustomData.of(tag)
            );

            // Returns the configured ItemStack together with its unique UUID
            return new CoreStackResult(
                    stack,
                    coreUuid
            );
        }

    // Resolves a matching core instance from the custom data tags of an item stack
    public static Optional<Core> getCoreFromItem(ItemStack stack) {
        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }

        CompoundTag tag = customData.copyTag();

        // Reads the core identifier string from the item nbt data as an Optional
        Optional<String> coreId = tag.getString("core_id");

        // Safely unwraps the Optional string and maps it to the registry lookup
        return coreId.flatMap(CoreRegistry::getByCoreId);
    }

    // Determines if the given item stack contains a valid core identifier tag
    public static boolean isCore(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return false;
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        // Checks the copied NBT structure for the core key without any object creation overhead
        return customData != null && customData.copyTag().contains("core_id");
    }

    // Determines if the given item stack template contains a valid core identifier tag
    public static boolean isCore(ItemStackTemplate template) {
        if (template == null) {
            return false;
        }

        // Direct fetch from the template - returns null if the component is missing
        CustomData customData = template.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }

        // Checks the copied NBT structure for the core key without creating an Optional wrapper
        return customData.copyTag().contains("core_id");
    }

    public static Optional<UUID> getCoreUuid(ItemStack stack) {

        if (stack == null
                || stack.isEmpty()
                || !stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }

        CustomData customData =
                stack.get(DataComponents.CUSTOM_DATA);

        if (customData == null) {
            return Optional.empty();
        }

        CompoundTag tag =
                customData.copyTag();

        Optional<String> uuidString =
                tag.getString("core_uuid");

        if (uuidString.isEmpty()) {
            return Optional.empty();
        }

        try {

            return Optional.of(
                    UUID.fromString(uuidString.get())
            );

        } catch (IllegalArgumentException exception) {

            return Optional.empty();
        }
    }
}