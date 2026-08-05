package de.ep.astralrelics.playerdata;

import de.ep.astralrelics.relic.RelicType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    // Store what relic is equipped in each slot (null means empty slot)
    private RelicType leftRelic;
    private RelicType rightRelic;

    // List of unique IDs of players who are trusted by this player
    private final List<UUID> trustedPlayers;

    // Constructor: Automatically runs when a new player profile is created
    public PlayerData() {
        this.leftRelic = null;
        this.rightRelic = null;
        this.trustedPlayers = new ArrayList<>();
    }

    // --- STANDARD GETTERS AND SETTERS ---

    public RelicType getLeftRelic() { return leftRelic; }
    public void setLeftRelic(RelicType relic) { this.leftRelic = relic; }

    public RelicType getRightRelic() { return rightRelic; }
    public void setRightRelic(RelicType relic) { this.rightRelic = relic; }

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
