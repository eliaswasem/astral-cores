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


    public static ItemStack createStack(Core core) {

        ItemStack stack = new ItemStack(
                core.getBaseItem()
        );


        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(
                        core.getName()
                )
        );


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


        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.of((float) core.getCustomModelData()),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        stack.set(DataComponents.MAX_STACK_SIZE, 1);


        CompoundTag tag = stack
                .getOrDefault(
                        DataComponents.CUSTOM_DATA,
                        CustomData.EMPTY
                )
                .copyTag();


        tag.putString(
                "core_id",
               core.getCoreId()
        );


        stack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
        );


        return stack;
    }



    public static Optional<Core> getCoreFromItem(ItemStack stack) {

        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }


        CompoundTag tag = Objects.requireNonNull(
                stack.get(DataComponents.CUSTOM_DATA)
        ).copyTag();


        Optional<String> coreId = tag.getString(
                "core_id"
        );


        return coreId.flatMap(CoreRegistry::getByCoreId);


    }
}