package de.ep.astralcores.command.untrust;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class UntrustCommandLogic {

    // Removes a player from the trust list and commits the update to the database
    public static int execute(CommandSourceStack source, ServerPlayer player, ServerPlayer target) {
        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        // Prevents players from running the command on themselves
        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(Component.literal("§cOperation invalid for self targets."));
            return 0;
        }

        PlayerData playerData = AstralCores.PLAYER_DATA.get(player);

        // Saves the profile if the target was found and successfully removed
        if (playerData.removeTrustedPlayer(targetUUID)) {
            AstralCores.PLAYER_DATA.save(player);
            source.sendSystemMessage(Component.literal("§eYou no longer trust " + target.getScoreboardName() + "."));
            return 1;
        }

        // Fails if the targeted player wasn't trusted to begin with
        source.sendFailure(Component.literal("§c" + target.getScoreboardName() + " was not on your trust list."));
        return 0;
    }
}
