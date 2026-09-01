package de.ep.astralcores.command.actionbar;

import com.mojang.brigadier.context.CommandContext;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.actionbar.ActionBarMode;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarCommandLogic {

    // Changes the player's action bar display layout mode and updates the HUD
    public static int execute(CommandContext<CommandSourceStack> context, ActionBarMode mode) {
        final ServerPlayer player;
        CommandSourceStack source = context.getSource();

        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            source.sendFailure(Component.literal("Failed to access your database profile."));
            return 0;
        }

        // Saves the chosen layout preference directly to the player profile data
        data.setActionBarMode(mode);

        // Updates the action bar display text immediately
        ActionBarManager.tick(player, data);

        source.sendSuccess(() -> Component.literal("Actionbar display layout updated to:" )
                .append(mode.name().toLowerCase()),
                false);
        return 1;
    }
}
