package de.ep.astralrelics.commands.trust;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TrustCommand {

    private final Map<UUID, Set<UUID>> trustMatrix = new ConcurrentHashMap<>();
    private final int maxTrustLimit;
    private Connection connection;

    public TrustCommand(File dataFolder, int maxTrustLimit) {
        this.maxTrustLimit = maxTrustLimit;
        initDatabase(dataFolder);
        loadDatabaseData();
    }

    private void initDatabase(File dataFolder) {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "trusts.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS relic_trusts (" +
                        "source_uuid VARCHAR(36) NOT NULL, " +
                        "target_uuid VARCHAR(36) NOT NULL, " +
                        "PRIMARY KEY (source_uuid, target_uuid));");
            }
        } catch (SQLException e) {
            System.err.println("[AstralRelics] Trust database initialization error: " + e.getMessage());
        }
    }

    private void loadDatabaseData() {
        if (connection == null) return;
        String sql = "SELECT source_uuid, target_uuid FROM relic_trusts";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID source = UUID.fromString(rs.getString("source_uuid"));
                UUID target = UUID.fromString(rs.getString("target_uuid"));
                trustMatrix.computeIfAbsent(source, k -> ConcurrentHashMap.newKeySet()).add(target);
            }
        } catch (SQLException e) {
            System.err.println("[AstralRelics] Failed to load trust data: " + e.getMessage());
        }
    }

    /**
     * Executes the logic for the "/trust" command action.
     * @return 0 = Error (Self-trust), 1 = Limit reached, 2 = Already on the list,
     *         3 = Request sent (waiting for reciprocity), 4 = Mutual alliance successfully established.
     */
    public int executeTrust(UUID player, UUID target) {
        if (player.equals(target)) return 0;

        Set<UUID> playerList = trustMatrix.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
        if (playerList.size() >= maxTrustLimit) return 1;
        if (!playerList.add(target)) return 2;

        // Save to database asynchronously to keep the main processing thread unblocked
        saveToDatabaseAsync(player, target);

        // Check for reciprocity (mutual trust verification)
        if (hasOneWayTrust(target, player)) {
            return 4; // Mutual alliance formed
        }
        return 3; // Request pending
    }

    /**
     * Executes the logic for the "/untrust" command action.
     * @return true if the target player was successfully removed from the player's trust list.
     */
    public boolean executeUntrust(UUID player, UUID target) {
        Set<UUID> playerList = trustMatrix.get(player);
        if (playerList != null && playerList.remove(target)) {
            deleteFromDatabaseAsync(player, target);
            return true;
        }
        return false;
    }

    /**
     * CORE MECHANIC FOR RELICS:
     * Returns true ONLY if BOTH players mutually trust each other.
     */
    public boolean isImmune(UUID playerA, UUID playerB) {
        return hasOneWayTrust(playerA, playerB) && hasOneWayTrust(playerB, playerA);
    }

    private boolean hasOneWayTrust(UUID owner, UUID target) {
        Set<UUID> list = trustMatrix.get(owner);
        return list != null && list.contains(target);
    }

    private void saveToDatabaseAsync(UUID source, UUID target) {
        new Thread(() -> {
            String sql = "INSERT OR IGNORE INTO relic_trusts (source_uuid, target_uuid) VALUES (?, ?);";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, source.toString());
                stmt.setString(2, target.toString());
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }).start();
    }

    private void deleteFromDatabaseAsync(UUID source, UUID target) {
        new Thread(() -> {
            String sql = "DELETE FROM relic_trusts WHERE source_uuid = ? AND target_uuid = ?;";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, source.toString());
                stmt.setString(2, target.toString());
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }).start();
    }

    public void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); } catch (Exception ignored) {}
    }
}
