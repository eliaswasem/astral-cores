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

    /*
     * Resolves a core and gives the generated ItemStack to the target player.
     */
    public static int execute(CommandSourceStack source, ServerPlayer target, String astralId) {

        try {
            Core core = CoreRegistry.getByCoreId(
                    astralId.toLowerCase(Locale.ROOT)
            ).orElseThrow(
                    () -> new IllegalArgumentException("Invalid Core ID")
            );


            ItemStack itemStack = CoreFactory.createStack(core);


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
}