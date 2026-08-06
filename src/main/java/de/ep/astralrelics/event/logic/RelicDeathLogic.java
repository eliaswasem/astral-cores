package de.ep.astralrelics.event.logic;

import de.ep.astralrelics.AstralRelics;
import de.ep.astralrelics.playerdata.PlayerData;
import de.ep.astralrelics.relic.RelicFactory;
import de.ep.astralrelics.relic.RelicRegistry;
import de.ep.astralrelics.relic.RelicType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RelicDeathLogic {

    /**
     * Inspects the profile configuration of a dying player at the exact moment of death.
     * Reconstructs physical item assets from the equipped left and right enum tracks,
     * ejects them into the level space immediately, and wipes the active data profile slots.
     *
     * @param player The dying ServerPlayer entity containing the active slot configurations.
     */
    public static void executeDeathDrop(ServerPlayer player) {
        /* Fetches the database profile cache mapping bound to the player profile instance */
        PlayerData data = AstralRelics.PLAYER_DATA.get(player);
        if (data == null) return;

        /* Processes the virtual left equipment track channel */
        if (data.getLeftRelic() != null) {
            dropRelicToWorld(player, data.getLeftRelic());
            data.setLeftRelic(null);
        }

        /* Processes the virtual right equipment track channel */
        if (data.getRightRelic() != null) {
            dropRelicToWorld(player, data.getRightRelic());
            data.setRightRelic(null);
        }

        /* Clears the client actionbar display cache instantly so it is completely empty upon respawning */
        //ActionBarUpdater.update(player);
    }

    /**
     * Resolves the template properties of a relic type and spawns it as an entity item in the world.
     */
    private static void dropRelicToWorld(ServerPlayer player, RelicType type) {
        RelicRegistry.get(type).ifPresent(relic -> {
            /* Requests the 1.21+ factory engine to compile a valid unstackable item asset with its persistent tags */
            ItemStack relicStack = RelicFactory.createStack(relic);

            /* Spawns the compiled item entity stack safely directly at the player's exact death location vectors */
            player.drop(relicStack, true, false);
        });
    }
}
