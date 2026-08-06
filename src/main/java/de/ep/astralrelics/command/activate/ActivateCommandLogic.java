package de.ep.astralrelics.command.activate;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import de.ep.astralrelics.relic.Relic;
import de.ep.astralrelics.relic.RelicRegistry;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ActivateCommandLogic {

    /**
     * Processes the core activation mechanics to trigger a relic ability from a command execution context.
     * Verifies profile slot entries, cross-references templates, and deploys the custom spell sequence.
     *
     * @param source     The command context wrapper used to echo feedback messages back down the chat pipeline.
     * @param player     The active ServerPlayer targeted by the context extraction routine who owns the slots.
     * @param isLeftSlot Flag determining whether the active loop should operate on the left or right account data.
     * @return An execution code where 1 represents successful completion and 0 safely signals a failure state.
     */
    public static int execute(CommandSourceStack source, ServerPlayer player, boolean isLeftSlot) {
        /* Fetches the current persistent data instance associated with the active player from the SQLite database cache */
        PlayerData data = AstralRelics.PLAYER_DATA.get(player);
        if (data == null) {
            source.sendFailure(Component.literal("§cFailed to access your database profile."));
            return 0;
        }

        /* Reads the current active Enum field state from the selected position in the player data layout model */
        RelicType targetedType = isLeftSlot ? data.getLeftRelic() : data.getRightRelic();

        /* If the resolved registry slot points to a null reference state, notify the client that nothing is equipped */
        if (targetedType == null) {
            source.sendFailure(Component.literal("§cYou do not have a relic equipped in that slot."));
            return 0;
        }

        /* Cross-references the active Enum value with the central registry layout map to obtain item data and spell methods */
        Relic relic = RelicRegistry.get(targetedType).orElse(null);
        if (relic == null) {
            source.sendFailure(Component.literal("§cCritical: Stored relic type mapping resolution failure."));
            return 0;
        }

        /* Fires the abstract executable spell block defined inside your individual core relic structures */
        relic.activate(player);

        return 1;
    }
}
