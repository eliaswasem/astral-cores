package de.ep.astralcores.actionbar;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.manager.CoreCooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarManager {

    // Updates the player action bar text based on their equipped core and cooldown status
    public static void tick(ServerPlayer player, PlayerData data) {
        // Gets the player preferred display mode
        ActionBarMode mode = (data != null) ? data.getActionBarMode() : ActionBarMode.ICON;

        // Shows empty slots if no core is equipped
        if (data == null || data.getEquippedCore() == null) {
            switch (mode) {
                case ICON -> sendPacket(player, "\uE000");
                case TEXT -> sendPacket(player, "§cNone");
            }
            return;
        }

        CoreType equippedType = data.getEquippedCore();
        Core core = CoreRegistry.get(equippedType).orElse(null);

        // Shows empty slots if the core registry fails
        if (core == null) {
            switch (mode) {
                case ICON -> sendPacket(player, "\uE000");
                case TEXT -> sendPacket(player, "§cNone");
            }
            return;
        }

        // Checks if the core has active or passive abilities
        boolean hasPassiveFeature = core.getPassiveCooldown() > 0;
        boolean hasActiveFeature = core.getActiveCooldown() > 0;

        boolean activeReady = CoreCooldownManager.isActiveReady(data, equippedType);
        boolean passiveReady = CoreCooldownManager.isPassiveReady(data, equippedType);

        int activeRemaining = CoreCooldownManager.getActiveRemaining(data, equippedType);
        int passiveRemaining = CoreCooldownManager.getPassiveRemaining(data, equippedType);

        // Formats the passive ability status string
        String passiveStatus = "";
        if (hasPassiveFeature) {
            passiveStatus = passiveReady ? "§aReady " : "§6" + formatTime(passiveRemaining) + " ";
        }

        // Formats the active ability status string
        String activeStatus = "";
        if (hasActiveFeature) {
            activeStatus = activeReady ? " §aReady" : " §c" + formatTime(activeRemaining);
        }

        // Selects the center text or icon based on player settings
        String centerModule;
        switch (mode) {
            case ICON -> centerModule = core.getCustomChar();
            case TEXT -> centerModule = core.getName();
            default -> centerModule = core.getName();
        }

        // Sends the combined text to the player action bar
        sendPacket(player, passiveStatus + centerModule + activeStatus);
    }

    // Sends the action bar packet to the player connection
    private static void sendPacket(ServerPlayer player, String content) {
        if (player.connection != null) {
            player.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(content)));
        }
    }

    // Formats seconds into MM:SS format or short shorthand text
    private static String formatTime(int totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // Forces an immediate real-time refresh of the player action bar
    public static void forceUpdate(ServerPlayer player) {
        PlayerData data = de.ep.astralcores.AstralCores.PLAYER_DATA.get(player);
        tick(player, data);
    }
}
