package de.ep.astralcores.playerdata;

import de.ep.astralcores.core.CoreType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    // Store what core is equipped in each slot (null means empty slot)
    private CoreType leftCore;
    private CoreType rightCore;

    // List of unique IDs of players who are trusted by this player
    private final List<UUID> trustedPlayers;

    // Constructor: Automatically runs when a new player profile is created
    public PlayerData() {
        this.leftCore = null;
        this.rightCore = null;
        this.trustedPlayers = new ArrayList<>();
    }

    // --- STANDARD GETTERS AND SETTERS ---

    public CoreType getLeftCore() { return leftCore; }
    public void setLeftCore(CoreType core) { this.leftCore = core; }

    public CoreType getRightCore() { return rightCore; }
    public void setRightCore(CoreType core) { this.rightCore = core; }

    // --- TRUSTED PLAYERS UTILITIES ---

    // Get the list of all trusted players
    public List<UUID> getTrustedPlayers() {
        return this.trustedPlayers;
    }

    // Add a friend to the trust list. Returns false if they were already added.
    public boolean addTrustedPlayer(UUID uuid) {
        if (!trustedPlayers.contains(uuid)) {
            trustedPlayers.add(uuid);
            return true;
        }
        return false;
    }

    // Remove a friend from the trust list.
    public boolean removeTrustedPlayer(UUID uuid) {
        return trustedPlayers.remove(uuid);
    }

    // Quick check to see if a specific player ID is trusted
    public boolean isTrusted(UUID uuid) {
        return trustedPlayers.contains(uuid);
    }
}
