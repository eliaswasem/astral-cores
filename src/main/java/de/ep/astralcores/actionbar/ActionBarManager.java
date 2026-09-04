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
                case ICON -> sendPacket(player, Component.literal("\uE000"));
                case TEXT -> sendPacket(player, Component.literal("None")
                        .withStyle(style -> style.withColor(0xFF5555)));
            }
            return;
        }

        CoreType equippedType = data.getEquippedCore();
        Core core = CoreRegistry.get(equippedType).orElse(null);

        // Shows empty slots if the core registry fails
        if (core == null) {
            switch (mode) {
                case ICON -> sendPacket(player, Component.literal("\uE000"));
                case TEXT -> sendPacket(player, Component.literal("None")
                        .withStyle(style -> style.withColor(0xFF5555)));
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
        Component passiveStatus = Component.empty();
        if (hasPassiveFeature) {
            passiveStatus = passiveReady
                    ? Component.literal("Ready ")
                    .withStyle(style -> style.withColor(0x55FF55))
                    : Component.literal(formatTime(passiveRemaining) + " ")
                    .withStyle(style -> style.withColor(0xFFAA00));
        }

        // Formats the active ability status string
        Component activeStatus = Component.empty();
        if (hasActiveFeature) {
            activeStatus = activeReady
                    ? Component.literal(" Ready")
                    .withStyle(style -> style.withColor(0x55FF55))
                    : Component.literal(" " + formatTime(activeRemaining))
                    .withStyle(style -> style.withColor(0xFF5555));
        }

        // Selects the center text or icon based on player settings
        Component centerModule;
        switch (mode) {
            case ICON -> centerModule = Component.literal(core.getCustomChar());
            case TEXT -> centerModule = core.getName();
            default -> centerModule = core.getName();
        }

        // Sends the combined text to the player action bar
        Component content = Component.empty()
                .append(passiveStatus)
                .append(centerModule)
                .append(activeStatus);

        sendPacket(player, content);
    }

    // Sends the action bar packet to the player connection
    private static void sendPacket(ServerPlayer player, Component content) {
        if (player.connection != null) {
            player.connection.send(new ClientboundSetActionBarTextPacket(content));
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