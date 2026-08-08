package de.ep.astralcores.manager;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarManager {

    /**
     * Updates the action bar text interface for a specific player based on their core status.
     * Enforces the sequential layout structure: [Passive-Status] [Core Name OR Icon] [Active-Status].
     * Dynamically omits passive tracking modules if the core defines no passive cooling window.
     *
     * @param player The ServerPlayer target profile context to evaluate.
     * @param data   The cached PlayerData reference provided by the central main loop.
     */
    public static void tick(ServerPlayer player, PlayerData data) {
        // Resolve the preferred layout choice or default to ICON if profile records are missing
        PlayerData.ActionBarMode mode = (data != null) ? data.getActionBarMode() : PlayerData.ActionBarMode.ICON;

        // Handle the absolute empty state where no core module instance is bound to the target slot
        if (data == null || data.getEquippedCore() == null) {
            switch (mode) {
                case ICON -> sendPacket(player, "\uE000"); // Displays the empty core container slot asset
                case TEXT -> sendPacket(player, "§cNone");   // Explicitly fallbacks to raw unequipped string
            }
            return;
        }

        CoreType equippedType = data.getEquippedCore();
        Core relic = CoreRegistry.get(equippedType).orElse(null);

        // Critical recovery routing if registry definition verification fails unexpectedly
        if (relic == null) {
            switch (mode) {
                case ICON -> sendPacket(player, "\uE000");
                case TEXT -> sendPacket(player, "§cNone");
            }
            return;
        }

        // --- CORE MODULE CAPABILITY BOUNDARY EVALUATIONS ---
        boolean hasPassiveFeature = relic.getPassiveCooldown() > 0;
        boolean hasActiveFeature = relic.getActiveCooldown() > 0;

        // Replaced player parameters with cached data profiles to prevent compilation failure
        boolean activeReady = CooldownManager.isActiveReady(data, equippedType);
        boolean passiveReady = CooldownManager.isPassiveReady(data, equippedType);

        int activeRemaining = CooldownManager.getActiveRemaining(data, equippedType);
        int passiveRemaining = CooldownManager.getPassiveRemaining(data, equippedType);

        // --- COMPONENT SEGMENT STRING COMPOSITION ---
        // Completely isolates the passive channel layout to prevent broken leading white spaces
        String passiveStatus = "";
        if (hasPassiveFeature) {
            passiveStatus = passiveReady ? "§aReady " : "§6" + formatTime(passiveRemaining) + " ";
        }

        String activeStatus = "";
        if (hasActiveFeature) {
            activeStatus = activeReady ? " §aReady" : " §c" + formatTime(activeRemaining);
        }

        // --- CORE IDENTITY MODULE INTERPOLATION ---
        String centerModule;
        switch (mode) {
            case ICON -> centerModule = relic.getCustomChar(); // Resolves localized resource font identifier
            case TEXT -> centerModule = relic.getName();       // Resolves configured display string name
            default -> centerModule = relic.getName();
        }

        // --- INTERFACE PIPELINE TRANSMISSION ---
        // Concat and stream out the compiled hud component package via the direct packet channel
        sendPacket(player, passiveStatus + centerModule + activeStatus);
    }

    /**
     * Enforces raw text parameters directly into the native client connection networking pipeline.
     */
    private static void sendPacket(ServerPlayer player, String content) {
        if (player.connection != null) {
            player.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(content)));
        }
    }

    /**
     * Converts raw integer tracking metrics into digital MM:SS formats or shorthand seconds.
     */
    private static String formatTime(int totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Public bridge method triggering an instant, real-time action bar layout forced update.
     */
    public static void forceUpdate(ServerPlayer player) {
        PlayerData data = de.ep.astralcores.AstralCores.PLAYER_DATA.get(player);
        tick(player, data);
    }
}
