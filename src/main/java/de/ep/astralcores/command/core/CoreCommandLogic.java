package de.ep.astralcores.command.core;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Optional;

public class CoreCommandLogic {

    public static int give(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();

        ServerPlayer target =
                EntityArgument.getPlayer(
                        context,
                        "target"
                );

        String coreId =
                context.getArgument(
                        "coreId",
                        String.class
                );

        Optional<Core> coreOptional =
                CoreRegistry.getByCoreId(
                        coreId.toLowerCase(Locale.ROOT)
                );

        if (coreOptional.isEmpty()) {
            source.sendFailure(
                    Component.literal(
                            "Unknown Core identifier: "
                    ).append(coreId)
            );

            return 0;
        }

        Core core = coreOptional.get();

        ItemStack itemStack =
                CoreFactory.createStack(core);

        if (!target.getInventory().add(itemStack)) {
            target.drop(itemStack, false);
        }

        source.sendSuccess(
                () -> Component.literal("Gave ")
                        .append(core.getName())
                        .append(" to ")
                        .append(target.getDisplayName())
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return 1;
    }

    public static int set(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();

        ServerPlayer target =
                EntityArgument.getPlayer(
                        context,
                        "target"
                );

        String coreId =
                context.getArgument(
                        "coreId",
                        String.class
                );

        Optional<Core> coreOptional =
                CoreRegistry.getByCoreId(
                        coreId.toLowerCase(Locale.ROOT)
                );

        if (coreOptional.isEmpty()) {
            source.sendFailure(
                    Component.literal(
                            "Unknown Core identifier: "
                    ).append(coreId)
            );

            return 0;
        }

        Core core = coreOptional.get();

        PlayerData data =
                AstralCores.PLAYER_DATA.get(target);

        if (data == null) {
            source.sendFailure(
                    Component.literal(
                            "Failed to resolve internal data container for the target player."
                    )
            );

            return 0;
        }

        data.setEquippedCore(core.getType());

        source.sendSuccess(
                () -> Component.literal("Successfully set ")
                        .append(target.getDisplayName())
                        .append("'s equipped core to ")
                        .append(core.getName())
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return 1;
    }

    public static int clear(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();

        ServerPlayer target =
                EntityArgument.getPlayer(
                        context,
                        "target"
                );

        PlayerData data =
                AstralCores.PLAYER_DATA.get(target);

        if (data == null) {
            source.sendFailure(
                    Component.literal(
                            "Failed to access the target's database profile."
                    )
            );

            return 0;
        }

        data.clearEquippedCore();

        source.sendSuccess(
                () -> Component.literal("Successfully cleared ")
                        .append(target.getScoreboardName())
                        .append("'s core")
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return 1;
    }

    public static int clearInventory(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();

        ServerPlayer target =
                EntityArgument.getPlayer(
                        context,
                        "target"
                );

        String coreId =
                context.getArgument(
                        "coreId",
                        String.class
                );

        PlayerData data =
                AstralCores.PLAYER_DATA.get(target);

        if (data == null) {
            source.sendFailure(
                    Component.literal(
                            "Failed to access the target's database profile."
                    )
            );

            return 0;
        }

        boolean clearAll =
                coreId.equals("*");

        Core targetCore = null;

        if (!clearAll) {
            Optional<Core> coreOptional =
                    CoreRegistry.getByCoreId(
                            coreId.toLowerCase(Locale.ROOT)
                    );

            if (coreOptional.isEmpty()) {
                source.sendFailure(
                        Component.literal(
                                "Unknown Core identifier: "
                        ).append(coreId)
                );

                return 0;
            }

            targetCore = coreOptional.get();
        }

        int removedCount = 0;

        for (int i = 0;
             i < target.getInventory().getContainerSize();
             i++) {

            ItemStack stack =
                    target.getInventory().getItem(i);

            if (!CoreFactory.isCore(stack)) {
                continue;
            }

            if (clearAll) {
                target.getInventory().removeItem(stack);
                removedCount++;
                continue;
            }

            Optional<Core> foundCore =
                    CoreFactory.getCoreFromItem(stack);

            if (foundCore.isPresent()
                    && foundCore.get()
                    .getCoreId()
                    .equalsIgnoreCase(
                            targetCore.getCoreId()
                    )) {

                target.getInventory().removeItem(stack);
                removedCount++;
            }
        }

        final int finalRemovedCount =
                removedCount;

        if (clearAll) {
            source.sendSuccess(
                    () -> Component.literal(
                                    "Successfully cleared all cores from "
                            )
                            .append(target.getDisplayName())
                            .append("'s inventory (Removed: ")
                            .append(String.valueOf(finalRemovedCount))
                            .append(")")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        } else {
            final Core finalTargetCore =
                    targetCore;

            source.sendSuccess(
                    () -> Component.literal(
                                    "Successfully cleared core "
                            )
                            .append(finalTargetCore.getName())
                            .append(" from ")
                            .append(target.getDisplayName())
                            .append("'s inventory (Removed: ")
                            .append(String.valueOf(finalRemovedCount))
                            .append(")")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        }

        target.containerMenu.broadcastChanges();

        return 1;
    }
}
