package de.ep.astralcores.playerdata;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class PlayerDataManager {

    // This HashMap caches the data in RAM so we don't spam database reads during normal gameplay
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    // The active database connection link
    private Connection connection;

    // GSON tool used ONLY to convert our UUID list into a single text line for easy SQL storage
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<ArrayList<String>>() {}.getType();

    // Constructor: This sets up the database file inside the world folder
    public PlayerDataManager(File worldFolder) {
        // KORRIGIERT: Ordnername von "astracores" zu "astralcores" geändert
        File dataFolder = new File(worldFolder, "astralcores");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        try {
            // Load the standard SQLite driver built into Java / Minecraft dependencies
            Class.forName("org.sqlite.JDBC");

            // Connect to (or create) the playerdata.db file
            File dbFile = new File(dataFolder, "playerdata.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            // Automatically execute SQL to build our storage table if it does not exist yet
            // KORRIGIERT: Tabellenname auf "player_cores" geändert
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS player_cores (" +
                                "uuid TEXT PRIMARY KEY, " +     // Unique Key: Player UUID string
                                "left_core TEXT, " +          // Store Enum name string
                                "right_core TEXT, " +         // Store Enum name string
                                "trusted_players TEXT)"        // Store List as a serialized text block
                );
            }
            AstralCores.LOGGER.info("AstralCores SQLite Database successfully loaded and healthy.");
        } catch (Exception e) {
            AstralCores.LOGGER.error("CRITICAL: Failed to initialize SQLite Database Engine!", e);
        }
    }

    // RUNS ON JOIN: Loads existing database records into our active RAM cache
    public void load(ServerPlayer player) {
        UUID uuid = player.getUUID();
        // KORRIGIERT: Tabellenname auf "player_cores" vereinheitlicht
        String query = "SELECT * FROM player_cores WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString()); // Replace the "?" in the query with the player's string UUID

            try (ResultSet rs = ps.executeQuery()) {
                PlayerData data = new PlayerData();

                // If a database row exists for this player, read it
                if (rs.next()) {
                    String left = rs.getString("left_core");
                    String right = rs.getString("right_core");
                    String trustedJson = rs.getString("trusted_players");

                    // Reconstruct core states from raw text back to Java Enums
                    if (left != null) data.setLeftCore(CoreType.valueOf(left));
                    if (right != null) data.setRightCore(CoreType.valueOf(right));

                    // Parse the JSON text string back into a standard list of Java UUIDs
                    if (trustedJson != null && !trustedJson.isEmpty()) {
                        List<String> trustedStrings = gson.fromJson(trustedJson, listType);
                        if (trustedStrings != null) {
                            for (String tUuid : trustedStrings) {
                                data.addTrustedPlayer(UUID.fromString(tUuid));
                            }
                        }
                    }
                    AstralCores.LOGGER.info("Loaded SQLite profile data for player: {}", player.getScoreboardName());
                } else {
                    // Brand new player detected: Create an empty row profile in the database
                    insertNewPlayer(uuid);
                    AstralCores.LOGGER.info("Created brand new database profile row for player: {}", player.getScoreboardName());
                }

                // Put the completed data into our RAM cache map for fast in-game utilization
                cache.put(uuid, data);
            }
        } catch (SQLException e) {
            AstralCores.LOGGER.error("SQL Exception caught during profile load routine for: {}", uuid, e);
            cache.put(uuid, new PlayerData()); // Safety fallback: Give them blank data so the server doesn't crash
        }
    }

    // RUNS IN GAME: Returns the rapid-access RAM cache data for active gameplay checks
    public PlayerData get(ServerPlayer player) {
        PlayerData data = cache.get(player.getUUID());
        if (data == null) {
            throw new IllegalStateException("RAM cache missed runtime check for active player entity: " + player.getUUID());
        }
        return data;
    }

    // RUNS PERIODICALLY / ON QUIT: Updates database file safely using current RAM values
    public void save(ServerPlayer player) {
        UUID uuid = player.getUUID();
        PlayerData data = cache.get(uuid);
        if (data == null) return; // Nothing cached, skip saving

        // KORRIGIERT: Tabelle auf "player_cores" und Spalten auf "left_core", "right_core" umgestellt
        String update = "UPDATE player_cores SET left_core = ?, right_core = ?, trusted_players = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(update)) {
            // Bind core Enum values or null strings if empty
            ps.setString(1, data.getLeftCore() != null ? data.getLeftCore().name() : null);
            ps.setString(2, data.getRightCore() != null ? data.getRightCore().name() : null);

            // Serialize trust context list into a compressed text line via GSON
            List<String> trustedStrings = new ArrayList<>();
            for (UUID tUuid : data.getTrustedPlayers()) {
                trustedStrings.add(tUuid.toString());
            }
            ps.setString(3, gson.toJson(trustedStrings));
            ps.setString(4, uuid.toString()); // Targeted Row

            ps.executeUpdate(); // Push updates down to database file safely
        } catch (SQLException e) {
            AstralCores.LOGGER.error("Failed executing SQLite push operation during state save for: {}", uuid, e);
        }
    }

    // RUNS ON QUIT: Saves data to disk and completely removes player from RAM to save memory
    public void unload(ServerPlayer player) {
        save(player);
        cache.remove(player.getUUID());
    }

    // Helper method to write an empty starter row for a fresh player profile
    private void insertNewPlayer(UUID uuid) throws SQLException {
        // KORRIGIERT: Tabelle auf "player_cores" und Spalten auf "left_core", "right_core" umgestellt
        String insert = "INSERT INTO player_cores (uuid, left_core, right_core, trusted_players) VALUES (?, NULL, NULL, '[]')";
        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    // RUNS ON SERVER SHUTDOWN: Terminates stream pipeline connection pool cleanly
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                AstralCores.LOGGER.info("SQLite storage stream safely terminated.");
            }
        } catch (SQLException e) {
            AstralCores.LOGGER.error("Critical stream termination failure on SQLite connection pool", e);
        }
    }
}
