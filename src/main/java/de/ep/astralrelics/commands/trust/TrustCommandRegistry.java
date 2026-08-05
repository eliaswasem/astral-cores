package de.ep.astralrelics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.ep.astralrelics.commands.trust.TrustCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class TrustCommandRegistry {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, TrustCommand core) {
        // Register: /trust <player>
        dispatcher.register(Commands.literal("trust")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> executeTrust(context, core))));

        // Register: /untrust <player>
        dispatcher.register(Commands.literal("untrust")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> executeUntrust(context, core))));
    }

    private static int executeTrust(CommandContext<CommandSourceStack> context, TrustCommand core) {
        CommandSourceStack source = context.getSource();

        // Use source.getPlayer() instead of getExecutor()
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        String targetName = StringArgumentType.getString(context, "player");
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);

        if (target == null) {
            source.sendFailure(Component.literal("Player '" + targetName + "' is not online."));
            return 0;
        }

        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        int result = core.executeTrust(playerUUID, targetUUID);

        switch (result) {
            case 0 -> source.sendFailure(Component.literal("§cYou cannot trust yourself."));
            case 1 -> source.sendFailure(Component.literal("§cYou have reached your trusted player limit."));
            case 2 -> source.sendSystemMessage(Component.literal("§eYou already trust " + target.getScoreboardName() + "."));
            case 3 -> {
                source.sendSystemMessage(Component.literal("§aTrust extended! Waiting for " + target.getScoreboardName() + " to type §e/trust " + player.getScoreboardName()));
                target.sendSystemMessage(Component.literal("§6" + player.getScoreboardName() + " wants to form a Relic Alliance! Type §e/trust " + player.getScoreboardName() + " §6to confirm."));
            }
            case 4 -> {
                source.sendSystemMessage(Component.literal("§b§lAlliance Sealed! §r§bYou and " + target.getScoreboardName() + " are now immune to each other's relics."));
                target.sendSystemMessage(Component.literal("§b§lAlliance Sealed! §r§bYou and " + player.getScoreboardName() + " are now immune to each other's relics."));
            }
        }
        return 1;
    }

    private static int executeUntrust(CommandContext<CommandSourceStack> context, TrustCommand core) {
        CommandSourceStack source = context.getSource();

        // Use source.getPlayer() instead of getExecutor()
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        String targetName = StringArgumentType.getString(context, "player");
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);

        if (target == null) {
            source.sendFailure(Component.literal("Player '" + targetName + "' is not online."));
            return 0;
        }

        UUID playerUUID = player.getUUID();
        UUID targetUUID = target.getUUID();

        if (core.executeUntrust(playerUUID, targetUUID)) {
            source.sendSystemMessage(Component.literal("§6You dissolved your trust bond with " + target.getScoreboardName() + "."));
            target.sendSystemMessage(Component.literal("§c" + player.getScoreboardName() + " broke the trust link. Mutual relic immunity lost."));
        } else {
            source.sendFailure(Component.literal("§cThat player is not on your trust list."));
        }
        return 1;
    }
}
