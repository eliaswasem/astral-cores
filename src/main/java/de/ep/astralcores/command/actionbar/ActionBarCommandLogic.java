package de.ep.astralcores.command.actionbar;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.actionbar.ActionBarMode;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommandLogic {

    // Changes the player's action bar display layout mode and updates the HUD
    public static int execute(CommandSourceStack source, ServerPlayer player, ActionBarMode mode) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        // Saves the chosen layout preference directly to the player profile data
        data.setActionBarMode(mode);

        // Updates the action bar display text immediately
        ActionBarManager.tick(player, data);

        source.sendSuccess(() -> Component.literal("§aActionbar display layout updated to: §e" + mode.name()), false);
        return 1;
    }
}
