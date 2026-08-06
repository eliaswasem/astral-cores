package de.ep.astralrelics.relic;

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

public class RelicFactory {


    public static ItemStack createStack(Relic relic) {

        ItemStack stack = new ItemStack(
                relic.getBaseItem()
        );


        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(
                        relic.getName()
                )
        );


        if (relic.getLore() != null && !relic.getLore().isEmpty()) {

            stack.set(
                    DataComponents.LORE,
                    new ItemLore(
                            relic.getLore()
                                    .stream()
                                    .map(Component::literal)
                                    .collect(Collectors.toList())
                    )
            );
        }


        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.of((float) relic.getCustomModelData()),
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
                "astral_id",
                relic.getAstralId()
        );


        stack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
        );


        return stack;
    }



    public static Optional<Relic> getRelicFromItem(ItemStack stack) {

        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }


        CompoundTag tag = Objects.requireNonNull(
                stack.get(DataComponents.CUSTOM_DATA)
        ).copyTag();


        Optional<String> astralId = tag.getString(
                "astral_id"
        );


        return astralId.flatMap(RelicRegistry::getByAstralId);


    }
}