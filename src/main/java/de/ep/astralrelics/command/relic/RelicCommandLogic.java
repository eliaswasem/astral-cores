package de.ep.astralrelics.command.relic;

import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicFactory;
import de.ep.astralrelics.relic.RelicRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class RelicCommandLogic {

    /*
     * Resolves a relic and gives the generated ItemStack to the target player.
     */
    public static int execute(CommandSourceStack source, ServerPlayer target, String astralId) {

        try {
            Relic relic = RelicRegistry.getByAstralId(
                    astralId.toLowerCase(Locale.ROOT)
            ).orElseThrow(
                    () -> new IllegalArgumentException("Invalid Relic ID")
            );


            ItemStack itemStack = RelicFactory.createStack(relic);


            if (!target.getInventory().add(itemStack)) {
                target.drop(itemStack, false);
            }


            source.sendSuccess(
                    () -> Component.literal(
                            "§aGave "
                                    + relic.getName()
                                    + " to "
                                    + target.getScoreboardName()
                    ),
                    true
            );

            return 1;

        } catch (IllegalArgumentException e) {

            source.sendFailure(
                    Component.literal(
                            "§cUnknown Relic identifier."
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