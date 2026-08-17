package de.ep.astralcores.command.core;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class CoreCommandLogic {

    // Resolves a matching core instance by string id and transfers it to the target player
    public static int execute(CommandSourceStack source, ServerPlayer target, String astralId, CoreCommandType coreCommandType) {

        return switch (coreCommandType) {
            case CoreCommandType.GIVE -> executeGive(source, target, astralId);
            default -> 0;
        };

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

    public enum CoreCommandType{
        GIVE
    }
}
