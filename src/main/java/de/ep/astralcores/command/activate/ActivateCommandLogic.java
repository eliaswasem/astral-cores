package de.ep.astralcores.command.activate;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommandLogic {

    // Process the core activation mechanics to trigger the equipped core active ability
    public static int execute(CommandSourceStack source, ServerPlayer player) {
        // Fetch cached player profile data
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        // Read the single equipped core slot
        CoreType targetedType = data.getEquippedCore();

        // Check if the slot is empty
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYou do not have a core equipped."));
            return 0;
        }

        // Cross-reference with registry to obtain the active instance
        Core relic = CoreRegistry.get(targetedType).orElse(null);
        if (relic == null) {
            source.sendFailure(Component.literal("§cCritical: Stored core type mapping resolution failure."));
            return 0;
        }

        // Trigger the active core ability
        relic.activate(player);

        return 1;
    }
}
