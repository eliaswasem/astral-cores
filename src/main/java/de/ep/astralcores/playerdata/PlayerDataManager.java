package de.ep.astralcores.playerdata;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData.ActionBarMode;
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

    // GSON tools used to convert maps and lists into flat text blocks for SQL table optimization
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
    private final Type cooldownMapType = new TypeToken<HashMap<CoreType, Integer>>() {}.getType();

    // Constructor: This sets up the database file inside the world folder
    public PlayerDataManager(File worldFolder) {
        File dataFolder = new File(worldFolder, "astralcores");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        try {
            // Load the standard SQLite driver built into Java / Minecraft dependencies
            Class.forName("org.sqlite.JDBC");

            // Connect to (or create) the playerdata.db file
            File dbFile = new File(dataFolder, "playerdata.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            // Automatically execute SQL to build or safety-patch our storage table layout
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS player_cores (" +
                                "uuid TEXT PRIMARY KEY, " +     // Unique Key: Player UUID string
                                "equipped_core TEXT, " +       // Bound equipment core slot enum identification
                                "trusted_players TEXT, " +     // Serialized trust permission tracking array
                                "actionbar_mode TEXT, " +      // Layout visualization state node toggle
                                "active_cooldowns TEXT, " +    // Persisted active capability timer map
                                "passive_cooldowns TEXT)"      // Persisted passive feature timer map
                );

                // Safety patches: Add tracking columns explicitly if the table was created by older mod builds
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN active_cooldowns TEXT DEFAULT '{}'"); } catch (SQLException ignored) {}
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN passive_cooldowns TEXT DEFAULT '{}'"); } catch (SQLException ignored) {}
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN actionbar_mode TEXT DEFAULT 'ICON'"); } catch (SQLException ignored) {}
            }
            AstralCores.LOGGER.info("AstralCores SQLite Database successfully loaded and healthy.");
        } catch (Exception e) {
            AstralCores.LOGGER.error("CRITICAL: Failed to initialize SQLite Database Engine!", e);
        }
    }

    // RUNS ON JOIN: Loads existing database records into our active RAM cache
    public void load(ServerPlayer player) {
        UUID uuid = player.getUUID();
        String query = "SELECT * FROM player_cores WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString()); // Replace the "?" in the query with the player's string UUID

            try (ResultSet rs = ps.executeQuery()) {
                PlayerData data = new PlayerData();

                // If a database row exists for this player, read it
                if (rs.next()) {
                    String equipped = rs.getString("equipped_core");
                    String trustedJson = rs.getString("trusted_players");
                    String modeString = rs.getString("actionbar_mode");
                    String activeCooldownsJson = rs.getString("active_cooldowns");   // Fetch active timers
                    String passiveCooldownsJson = rs.getString("passive_cooldowns"); // Fetch passive timers

                    // Reconstruct core state from raw text back to Java Enum
                    if (equipped != null) data.setEquippedCore(CoreType.valueOf(equipped));

                    // Parse the JSON text string back into a standard list of Java UUIDs
                    if (trustedJson != null && !trustedJson.isEmpty()) {
                        List<String> trustedStrings = gson.fromJson(trustedJson, listType);
                        if (trustedStrings != null) {
                            for (String tUuid : trustedStrings) {
                                data.addTrustedPlayer(UUID.fromString(tUuid));
                            }
                        }
                    }

                    // Convert the stored database string token back into the native Enum mode safely
                    if (modeString != null) {
                        try {
                            data.setActionBarMode(ActionBarMode.valueOf(modeString));
                        } catch (IllegalArgumentException e) {
                            data.setActionBarMode(ActionBarMode.ICON);
                        }
                    }

                    // Deserializes stored active cooldown parameters directly into active RAM storage
                    if (activeCooldownsJson != null && !activeCooldownsJson.isEmpty()) {
                        Map<CoreType, Integer> activeMap = gson.fromJson(activeCooldownsJson, cooldownMapType);
                        if (activeMap != null) data.getActiveCooldownsMap().putAll(activeMap);
                    }

                    // Deserializes stored passive cooldown parameters directly into active RAM storage
                    if (passiveCooldownsJson != null && !passiveCooldownsJson.isEmpty()) {
                        Map<CoreType, Integer> passiveMap = gson.fromJson(passiveCooldownsJson, cooldownMapType);
                        if (passiveMap != null) data.getPassiveCooldownsMap().putAll(passiveMap);
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

        // Added active_cooldowns and passive_cooldowns tracking serialization slots into the pipeline
        String update = "UPDATE player_cores SET equipped_core = ?, trusted_players = ?, actionbar_mode = ?, active_cooldowns = ?, passive_cooldowns = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(update)) {
            // Bind core Enum value or null string if empty
            ps.setString(1, data.getEquippedCore() != null ? data.getEquippedCore().name() : null);

            // Serialize trust context list into a compressed text line via GSON
            List<String> trustedStrings = new ArrayList<>();
            for (UUID tUuid : data.getTrustedPlayers()) {
                trustedStrings.add(tUuid.toString());
            }
            ps.setString(2, gson.toJson(trustedStrings));

            // Push the exact active layout state Enum name down to the SQL stack
            ps.setString(3, data.getActionBarMode().name());

            // Serialize active timers map into raw JSON text lines
            ps.setString(4, gson.toJson(data.getActiveCooldownsMap()));

            // Serialize passive timers map into raw JSON text lines
            ps.setString(5, gson.toJson(data.getPassiveCooldownsMap()));

            ps.setString(6, uuid.toString()); // Targeted Row

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
        // Expanded basic framework insertions to cover default values for timer columns
        String insert = "INSERT INTO player_cores (uuid, equipped_core, trusted_players, actionbar_mode, active_cooldowns, passive_cooldowns) VALUES (?, NULL, '[]', 'ICON', '{}', '{}')";
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
