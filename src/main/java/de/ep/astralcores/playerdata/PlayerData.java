package de.ep.astralcores.playerdata;

import de.ep.astralcores.core.CoreType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    /**
     * Supported rendering configuration layouts for the active user action bar interface.
     */
    public enum ActionBarMode {
        TEXT,
        ICON
    }

    // Store what core is equipped in the slot (null means empty slot)
    private CoreType equippedCore;

    // List of unique IDs of players who are trusted by this player
    private final List<UUID> trustedPlayers;

    // Map tracking active capability cooldowns in seconds per specific core type
    private final Map<CoreType, Integer> activeCooldowns;

    // Map tracking passive recovery channel cooldowns in seconds per specific core type
    private final Map<CoreType, Integer> passiveCooldowns;

    // Personal display configuration preference controlling action bar visuals
    private ActionBarMode actionBarMode;

    // Constructor: Automatically runs when a new player profile is created
    public PlayerData() {
        this.equippedCore = null;
        this.trustedPlayers = new ArrayList<>();
        this.activeCooldowns = new HashMap<>();
        this.passiveCooldowns = new HashMap<>();
        this.actionBarMode = ActionBarMode.ICON; // Standard interface layout baseline
    }

    // --- COOLDOWN MANAGEMENT UTILITIES ---

    /**
     * Retrieves the entire backing map instance tracking active capability capabilities.
     * Required internally by data persistence layers for GSON serialization structures.
     *
     * @return The raw active cooldown reference tracking map.
     */
    public Map<CoreType, Integer> getActiveCooldownsMap() {
        return this.activeCooldowns;
    }

    /**
     * Retrieves the entire backing map instance tracking passive recovery capabilities.
     * Required internally by data persistence layers for GSON serialization structures.
     *
     * @return The raw passive cooldown reference tracking map.
     */
    public Map<CoreType, Integer> getPassiveCooldownsMap() {
        return this.passiveCooldowns;
    }

    /**
     * Ticks down all stored active and passive cooldown intervals by one second.
     * Automatically purges expired tracking nodes from memory loops.
     */
    public void tickCooldowns() {
        activeCooldowns.replaceAll((type, seconds) -> seconds - 1);
        activeCooldowns.values().removeIf(seconds -> seconds <= 0);

        passiveCooldowns.replaceAll((type, seconds) -> seconds - 1);
        passiveCooldowns.values().removeIf(seconds -> seconds <= 0);
    }

    // --- ACTION BAR CONFIGURATION UTILITIES ---

    /**
     * Retrieves the current action bar formatting preference for this profile.
     *
     * @return The active ActionBarMode configuration node.
     */
    public ActionBarMode getActionBarMode() {
        return this.actionBarMode;
    }

    /**
     * Updates the action bar interface layout directly utilizing the internal Enum state.
     *
     * @param mode The targeted ActionBarMode configuration preference.
     */
    public void setActionBarMode(ActionBarMode mode) {
        if (mode != null) {
            this.actionBarMode = mode;
        }
    }

    // --- STANDARD GETTERS AND SETTERS ---

    public CoreType getEquippedCore() {
        return equippedCore;
    }

    public void setEquippedCore(CoreType core) {
        this.equippedCore = core;
    }

    // --- TRUSTED PLAYERS UTILITIES ---

    public List<UUID> getTrustedPlayers() {
        return this.trustedPlayers;
    }

    public boolean addTrustedPlayer(UUID uuid) {
        if (!trustedPlayers.contains(uuid)) {
            trustedPlayers.add(uuid);
            return true;
        }
        return false;
    }

    public boolean removeTrustedPlayer(UUID uuid) {
        return trustedPlayers.remove(uuid);
    }

    public boolean isTrusted(UUID uuid) {
        return trustedPlayers.contains(uuid);
    }
}
