package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CoreDeathLogic {

    /**
     * Inspects the profile configuration of a dying player at the exact moment of death.
     * Reconstructs physical item assets from the equipped left and right enum tracks,
     * ejects them into the level space immediately, and wipes the active data profile slots.
     *
     * @param player The dying ServerPlayer entity containing the active slot configurations.
     */
    public static void executeDeathDrop(ServerPlayer player) {
        /* Fetches the database profile cache mapping bound to the player profile instance */
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) return;

        /* Processes the virtual left equipment track channel */
        if (data.getLeftCore() != null) {
            dropCoreToWorld(player, data.getLeftCore());
            data.setLeftCore(null);
        }

        /* Processes the virtual right equipment track channel */
        if (data.getRightCore() != null) {
            dropCoreToWorld(player, data.getRightCore());
            data.setRightCore(null);
        }

        /* Clears the client actionbar display cache instantly so it is completely empty upon respawning */
        //ActionBarUpdater.update(player);
    }

    /**
     * Resolves the template properties of a core type and spawns it as an entity item in the world.
     */
    private static void dropCoreToWorld(ServerPlayer player, CoreType type) {
        CoreRegistry.get(type).ifPresent(core -> {
            /* Requests the core factory to compile a valid unstackable item asset with its persistent tags */
            ItemStack coreStack = CoreFactory.createStack(core);

            /* Spawns the compiled item entity stack safely directly at the player's exact death location vectors */
            player.drop(coreStack, true, false);
        });
    }
}
