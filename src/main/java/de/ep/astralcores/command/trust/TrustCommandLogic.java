package de.ep.astralcores.command.trust;

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

public class TrustCommandLogic {

    public static int execute(
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

        // Prevents players from running the command on themselves
        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(
                    Component.literal(
                            "You cannot trust yourself."
                    )
            );

            return 0;
        }

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            return 0;
        }

        if (data.isTrusted(targetUUID)) {
            source.sendSystemMessage(
                    Component.literal(
                                    "You already trust "
                            )
                            .append(target.getScoreboardName())
                            .append(".")
                            .withStyle(
                                    ChatFormatting.YELLOW
                            )
            );

            return 0;
        }

        if (!data.addTrustedPlayer(targetUUID)) {
            return 0;
        }

        AstralCores.PLAYER_DATA.save(player);

        source.sendSystemMessage(
                Component.literal(
                                "You now trust "
                        )
                        .append(target.getScoreboardName())
                        .append(".")
                        .withStyle(
                                ChatFormatting.GREEN
                        )
        );

        return 1;
    }
}
