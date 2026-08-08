package de.ep.astralcores.playerdata;

import de.ep.astralcores.core.CoreType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    // Store what core is equipped in the slot (null means empty slot)
    private CoreType equippedCore;

    // List of unique IDs of players who are trusted by this player
    private final List<UUID> trustedPlayers;

    // Map tracking active capability cooldowns in seconds per specific core type
    private final Map<CoreType, Integer> activeCooldowns;

    // Map tracking passive recovery channel cooldowns in seconds per specific core type
    private final Map<CoreType, Integer> passiveCooldowns;

    // Constructor: Automatically runs when a new player profile is created
    public PlayerData() {
        this.equippedCore = null;
        this.trustedPlayers = new ArrayList<>();
        this.activeCooldowns = new HashMap<>();
        this.passiveCooldowns = new HashMap<>();
    }

    // --- COOLDOWN MANAGEMENT UTILITIES ---

    /**
     * Retrieves the remaining active cooldown seconds for a specific core type.
     * Returns 0 if no cooldown entry is active.
     */
    public int getActiveCooldownSeconds(CoreType type) {
        return activeCooldowns.getOrDefault(type, 0);
    }

    /**
     * Updates the remaining active cooldown seconds for a specific core type.
     * Removes the mapping if the duration reaches zero or lower to save memory overhead.
     */
    public void setActiveCooldownSeconds(CoreType type, int seconds) {
        if (seconds <= 0) {
            activeCooldowns.remove(type);
        } else {
            activeCooldowns.put(type, seconds);
        }
    }

    /**
     * Retrieves the remaining passive cooldown seconds for a specific core type.
     * Returns 0 if no cooldown entry is active.
     */
    public int getPassiveCooldownSeconds(CoreType type) {
        return passiveCooldowns.getOrDefault(type, 0);
    }

    /**
     * Updates the remaining passive cooldown seconds for a specific core type.
     * Removes the mapping if the duration reaches zero or lower to save memory overhead.
     */
    public void setPassiveCooldownSeconds(CoreType type, int seconds) {
        if (seconds <= 0) {
            passiveCooldowns.remove(type);
        } else {
            passiveCooldowns.put(type, seconds);
        }
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
