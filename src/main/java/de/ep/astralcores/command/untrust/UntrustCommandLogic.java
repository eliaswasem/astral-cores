package de.ep.astralcores.command.untrust;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class UntrustCommandLogic {

    public static int untrust(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {

        CommandSourceStack source =
                context.getSource();

        ServerPlayer player =
                source.getPlayerOrException();

        ServerPlayer target =
                EntityArgument.getPlayer(
                        context,
                        "player"
                );

        UUID playerUUID =
                player.getUUID();

        UUID targetUUID =
                target.getUUID();

        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(
                    Component.literal(
                            "You cannot untrust yourself."
                    )
            );

            return 0;
        }

        PlayerData playerData =
                AstralCores.PLAYER_DATA.get(player);

        if (playerData == null) {
            source.sendFailure(
                    Component.literal(
                            "Failed to access your database profile."
                    )
            );

            return 0;
        }

        if (!playerData.removeTrustedPlayer(targetUUID)) {
            source.sendFailure(
                    Component.empty()
                            .append(target.getDisplayName())
                            .append(
                                    " was not on your trust list."
                            )
            );

            return 0;
        }

        AstralCores.PLAYER_DATA.save(player);

        source.sendSuccess(
                () -> Component.literal(
                                "You no longer trust "
                        )
                        .append(target.getDisplayName())
                        .append(".")
                        .withStyle(
                                ChatFormatting.GREEN
                        ),
                false
        );

        return 1;
    }
}