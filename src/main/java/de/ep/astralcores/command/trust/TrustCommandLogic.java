package de.ep.astralcores.command.trust;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class TrustCommandLogic {

    // Adds a player to the trust list and commits the profile to the database
    public static int execute(CommandSourceStack source, ServerPlayer player, ServerPlayer target) {
        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        // Prevents players from running the command on themselves
        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(Component.literal("You cannot trust yourself."));
            return 0;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            return 0;
        }

        if( data.isTrusted(targetUUID)) {
            // Stops execution if the targeted player is already on the trust list
            source.sendSystemMessage(Component.literal("You already trust ")
                    .append(target.getScoreboardName())
                    .append(".")
                    .withStyle(ChatFormatting.YELLOW)
            );
            return 0;
        }

        // Saves the profile if the target was successfully added to the list
        if (data.addTrustedPlayer(targetUUID)) {
            AstralCores.PLAYER_DATA.save(player);
            source.sendSystemMessage(
                    Component.literal("You now trust ")
                            .append(target.getScoreboardName())
                            .append(".")
                            .withStyle(ChatFormatting.GREEN)
            );
            return 1;
        }

        return 0;
    }
}
