package de.ep.astralcores.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class FoodUtils {

    public static boolean isEatingEdibleItem(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }

        ItemStack stack = player.getUseItem();
        if (stack.isEmpty()) {
            return false;
        }

        return stack.has(DataComponents.FOOD);
    }

    public static boolean isFinishedEating(Player player) {
        return isEatingEdibleItem(player) && player.getUseItemRemainingTicks() == 1;
    }


    public static int getNutritionValue(Player player) {
        if (!isEatingEdibleItem(player)) {
            return 0;
        }

        FoodProperties properties = player.getUseItem().get(DataComponents.FOOD);
        return properties != null ? properties.nutrition() : 0;
    }
}
