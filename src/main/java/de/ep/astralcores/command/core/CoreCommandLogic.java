package de.ep.astralcores.command.core;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Optional;

public class CoreCommandLogic {

    // Resolves a matching core instance by string id and transfers it to the target player
    public static int execute(CommandSourceStack source, ServerPlayer target, String astralId, CoreCommandType coreCommandType) {

        return switch (coreCommandType) {
            case CoreCommandType.GIVE -> executeGive(source, target, astralId);
            case CoreCommandType.SET -> executeSet(source, target, astralId);
            case CoreCommandType.CLEAR -> executeClear(source, target);
            case CoreCommandType.CLEAR_INV -> executeClearInv(source, target, astralId);
        };

    }

    private static int executeClearInv(CommandSourceStack source, ServerPlayer target, String astralId) {
        PlayerData data = AstralCores.PLAYER_DATA.get(target);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to resolve internal data container for the target player."));
            return 0;
        }

        int removedCount = 0;
        boolean clearAll = astralId.equals("*");

        Core targetCore = null;
        if (!clearAll) {
            Optional<Core> coreOptional = CoreRegistry.getByCoreId(astralId.toLowerCase(Locale.ROOT));
            if (coreOptional.isEmpty()) {
                source.sendFailure(Component.literal("§cUnknown Core identifier: " + astralId));
                return 0;
            }
            targetCore = coreOptional.get();
        }

        // Iterate through the player's entire inventory container
        for (int i = 0; i < target.getInventory().getContainerSize(); i++) {
            ItemStack stack = target.getInventory().getItem(i);

            // Validate if the current item stack is a registered core
            if (CoreFactory.isCore(stack)) {
                if (clearAll) {
                    // Remove the item using the player inventory's internal reference clear
                    target.getInventory().removeItem(stack);
                    removedCount++;
                } else {
                    Optional<Core> foundCore = CoreFactory.getCoreFromItem(stack);
                    // Check if the extracted core id matches the specified target core id
                    if (foundCore.isPresent() && foundCore.get().getCoreId().equalsIgnoreCase(targetCore.getCoreId())) {
                        target.getInventory().removeItem(stack);
                        removedCount++;
                    }
                }
            }
        }

        // Create final copies of variables to safely pass them into the lambda expressions below
        final int finalRemovedCount = removedCount;

        // Send success feedback message without modifying the equipped core state
        if (clearAll) {
            source.sendSuccess(
                    () -> Component.literal("§aSuccessfully cleared all cores from " + target.getScoreboardName() + "'s inventory (Removed: " + finalRemovedCount + ")"),
                    true
            );
        } else {
            final Core finalTargetCore = targetCore;
            source.sendSuccess(
                    () -> Component.literal("§aSuccessfully cleared core " + finalTargetCore.getName() + " from " + target.getScoreboardName() + "'s inventory (Removed: " + finalRemovedCount + ")"),
                    true
            );
        }

        // Synchronize the client inventory layout to prevent ghost items
        target.containerMenu.broadcastChanges();
        return 1;
    }

    private static int executeClear(CommandSourceStack source, ServerPlayer target) {
        // Fetch the target player's data profile
        PlayerData data = AstralCores.PLAYER_DATA.get(target);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to resolve internal data container for the target player."));
            return 0;
        }

        // Equips the shared instance to the player
        // (This links the player to the global core object)
        data.clearEquippedCore();

        // Confirms success in chat
        source.sendSuccess(
                () -> Component.literal(
                        "§aSuccessfully cleared "
                                + target.getScoreboardName()
                                + "'s core"
                ),
                true
        );

        return 1;

    }

    private static int executeGive(CommandSourceStack source, ServerPlayer target, String astralId) {
        try {
            // Looks up the requested core inside the registry mapping
            Core core = CoreRegistry.getByCoreId(
                    astralId.toLowerCase(Locale.ROOT)
            ).orElseThrow(
                    () -> new IllegalArgumentException("Invalid Core ID")
            );

            // Generates the physical item stack for the requested core
            ItemStack itemStack = CoreFactory.createStack(core);

            // Adds the item stack directly to the player inventory or drops it if full
            if (!target.getInventory().add(itemStack)) {
                target.drop(itemStack, false);
            }

            source.sendSuccess(
                    () -> Component.literal(
                            "§aGave "
                                    + core.getName()
                                    + " to "
                                    + target.getScoreboardName()
                    ),
                    true
            );

            return 1;

        } catch (IllegalArgumentException e) {

            // Fails if the requested core string id does not exist in the registry
            source.sendFailure(
                    Component.literal(
                            "§cUnknown Core identifier."
                    )
            );

            return 0;

        } catch (Exception e) {

            source.sendFailure(
                    Component.literal(
                            "§cExecution failure."
                    )
            );

            return 0;
        }
    }

    public static int executeSet(CommandSourceStack source, ServerPlayer target, String astralId) {

        //. Fetch the single, shared global core instance from the registry
        Optional<Core> coreOptional = CoreRegistry.getByCoreId(astralId);

        // Validate existence (if empty, the ID doesn't exist in the system)
        if (coreOptional.isEmpty()) {
            source.sendFailure(Component.literal("§cUnknown Core identifier: " + astralId));
            return 0;
        }

        // Get the shared instance (safe to use across multiple players)
        Core core = coreOptional.get();

        // Fetch the target player's data profile
        PlayerData data = AstralCores.PLAYER_DATA.get(target);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to resolve internal data container for the target player."));
            return 0;
        }

        // Equip the shared instance to the player
        // (This links the player to the global core object)
        data.setEquippedCore(core.getType());

        // 6. Confirm success in chat
        source.sendSuccess(
                () -> Component.literal(
                        "§aSuccessfully set "
                                + target.getScoreboardName()
                                + "'s equipped core to "
                                + core.getName()
                ),
                true
        );

        return 1;
    }

    public enum CoreCommandType{
        GIVE,
        SET,
        CLEAR,
        CLEAR_INV
    }
}
