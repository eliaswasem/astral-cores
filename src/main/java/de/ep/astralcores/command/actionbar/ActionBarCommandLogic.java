package de.ep.astralcores.command.actionbar;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.ActionBarManager;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.playerdata.PlayerData.ActionBarMode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommandLogic {

    // Process the layout switch mechanics directly updating the targeted player data profile
    public static int execute(CommandSourceStack source, ServerPlayer player, ActionBarMode mode) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        // Apply the chosen Enum configuration preference straight to the user profile
        data.setActionBarMode(mode);
        // Instantly update Actionbar
        ActionBarManager.tick(player, data);

        // Send validation tracking feedback directly back to the executing user stack
        source.sendSuccess(() -> Component.literal("§aActionbar display layout updated to: §e" + mode.name()), false);
        return 1;
    }
}
