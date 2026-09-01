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

    public static int setText(
            CommandContext<CommandSourceStack> context
    ) {
        return setMode(
                context,
                ActionBarMode.TEXT
        );
    }

    public static int setIcon(
            CommandContext<CommandSourceStack> context
    ) {
        return setMode(
                context,
                ActionBarMode.ICON
        );
    }

    private static int setMode(
            CommandContext<CommandSourceStack> context,
            ActionBarMode mode
    ) {
        CommandSourceStack source =
                context.getSource();

        ServerPlayer player;

        try {
            player =
                    source.getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        if (data == null) {
            source.sendFailure(
                    Component.literal(
                            "Failed to access your database profile."
                    )
            );

            return 0;
        }

        data.setActionBarMode(mode);

        ActionBarManager.tick(
                player,
                data
        );

        source.sendSuccess(
                () -> Component.literal(
                                "Actionbar display layout updated to: "
                        )
                        .append(
                                mode.name().toLowerCase()
                        ),
                false
        );

        return 1;
    }
}
