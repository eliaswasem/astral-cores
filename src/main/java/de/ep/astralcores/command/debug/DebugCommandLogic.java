package de.ep.astralcores.command.debug;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DebugCommandLogic {

    public static int execute(
            CommandSourceStack source,
            ServerPlayer player,
            DebugCommandType debugCommandType
    ) {

        return switch (debugCommandType) {
            case DebugCommandType.COOLDOWN -> executeCooldownCommand(source, player);
            default -> 0;
        };
    }

    private static int executeCooldownCommand(CommandSourceStack source, ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        CooldownManager.resetCooldowns(data);

        source.sendSuccess(
                () -> Component.literal("All core cooldowns have been reset."),
                false
        );

        return 1;
    }

    public enum DebugCommandType {
        COOLDOWN
    }
}