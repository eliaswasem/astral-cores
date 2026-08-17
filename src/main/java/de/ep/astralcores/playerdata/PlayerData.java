package de.ep.astralcores.playerdata;

import de.ep.astralcores.actionbar.ActionBarMode;
import de.ep.astralcores.core.CoreType;

import java.util.*;

public class PlayerData {

    // Stores the currently equipped core type (null means empty slot)
    private CoreType equippedCore;

    // Holds the unique IDs of players trusted by this profile owner
    private final List<UUID> trustedPlayers;

    // Tracks active capability cooldown timers in seconds per core type
    private final Map<CoreType, Integer> activeCooldowns;

    // Tracks passive ability cooldown timers in seconds per core type
    private final Map<CoreType, Integer> passiveCooldowns;

    // Stores the preferred visual display format for the action bar
    private ActionBarMode actionBarMode;

    // Initializes default blank data configurations for a new profile
    public PlayerData() {
        this.equippedCore = null;
        this.trustedPlayers = new ArrayList<>();
        this.activeCooldowns = new HashMap<>();
        this.passiveCooldowns = new HashMap<>();
        this.actionBarMode = ActionBarMode.ICON;
    }

    // --- COOLDOWN MANAGEMENT UTILITIES ---

    // Returns the map tracking active capability cooldowns for serialization
    public Map<CoreType, Integer> getActiveCooldownsMap() {
        return this.activeCooldowns;
    }

    // Returns the map tracking passive ability cooldowns for serialization
    public Map<CoreType, Integer> getPassiveCooldownsMap() {
        return this.passiveCooldowns;
    }

    // Decrements all active and passive cooldown intervals by one second
    public void tickCooldowns() {
        activeCooldowns.replaceAll((type, seconds) -> seconds - 1);
        activeCooldowns.values().removeIf(seconds -> seconds <= 0);

        passiveCooldowns.replaceAll((type, seconds) -> seconds - 1);
        passiveCooldowns.values().removeIf(seconds -> seconds <= 0);
    }

    // --- ACTION BAR CONFIGURATION UTILITIES ---

    // Gets the current action bar display preference
    public ActionBarMode getActionBarMode() {
        return this.actionBarMode;
    }

    // Sets the action bar layout mode configuration
    public void setActionBarMode(ActionBarMode mode) {
        if (mode != null) {
            this.actionBarMode = mode;
        }
    }

    // --- STANDARD GETTERS AND SETTERS ---

    // Gets the equipped core type
    public CoreType getEquippedCore() {
        return equippedCore;
    }

    // Sets the equipped core type
    public void setEquippedCore(CoreType core) {
        this.equippedCore = core;
    }

    // --- TRUSTED PLAYERS UTILITIES ---

    // Gets the full list of trusted player UUIDs
    public List<UUID> getTrustedPlayers() {
        return this.trustedPlayers;
    }

    // Adds a player UUID to the trusted list if not already present
    public boolean addTrustedPlayer(UUID uuid) {
        if (!trustedPlayers.contains(uuid)) {
            trustedPlayers.add(uuid);
            return true;
        }
        return false;
    }

    // Removes a player UUID from the trusted list
    public boolean removeTrustedPlayer(UUID uuid) {
        return trustedPlayers.remove(uuid);
    }

    // Checks if a specific player UUID is trusted
    public boolean isTrusted(UUID uuid) {
        return trustedPlayers.contains(uuid);
    }
}
