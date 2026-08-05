package de.ep.astralrelics.command.trust;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class TrustCommandLogic {

    private static final int MAX_TRUST_LIMIT = 5;

    /**
     * Processes inventory boundaries, status lists, and data persistence tasks.
     */
    public static int execute(CommandSourceStack source, ServerPlayer player, ServerPlayer target) {
        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        if (playerUUID.equals(targetUUID)) {
            source.sendFailure(Component.literal("§cYou cannot trust yourself."));
            return 0;
        }

        PlayerData playerData = AstralRelics.PLAYER_DATA.get(player);

        if (playerData.getTrustedPlayers().size() >= MAX_TRUST_LIMIT) {
            source.sendFailure(Component.literal("§cYou have reached your trusted player limit."));
            return 0;
        }

        if (playerData.addTrustedPlayer(targetUUID)) {
            AstralRelics.PLAYER_DATA.save(player);
            source.sendSystemMessage(Component.literal("§aYou now trust " + target.getScoreboardName() + "."));
            return 1;
        }

        source.sendSystemMessage(Component.literal("§eYou already trust " + target.getScoreboardName() + "."));
        return 0;
    }
}
