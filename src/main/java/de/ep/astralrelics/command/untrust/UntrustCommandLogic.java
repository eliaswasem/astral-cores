package de.ep.astralrelics.command.untrust;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class UntrustCommandLogic {

    /**
     * Processes entry row extraction and validation changes.
     */
    public static int execute(CommandSourceStack source, ServerPlayer player, ServerPlayer target) {
        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(Component.literal("§cOperation invalid for self targets."));
            return 0;
        }

        PlayerData playerData = AstralRelics.PLAYER_DATA.get(player);

        if (playerData.removeTrustedPlayer(targetUUID)) {
            AstralRelics.PLAYER_DATA.save(player);
            source.sendSystemMessage(Component.literal("§eYou no longer trust " + target.getScoreboardName() + "."));
            return 1;
        }

        source.sendFailure(Component.literal("§c" + target.getScoreboardName() + " was not on your trust list."));
        return 0;
    }
}
