package de.ep.astralcores.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarMode;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class PlayerDataManager {

    // Caches player data in RAM to prevent database read spikes during gameplay
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    // Holds the active connection link to the database
    private Connection connection;

    // Google GSON utilities used to convert objects into JSON strings for database layout optimization
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
    private final Type cooldownMapType = new TypeToken<HashMap<CoreType, Integer>>() {}.getType();

    // Initializes database folders and patches table schemas
    public PlayerDataManager(File worldFolder) {
        File dataFolder = new File(worldFolder, "astralcores");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        try {
            Class.forName("org.sqlite.JDBC");

            File dbFile = new File(dataFolder, "playerdata.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            // Builds or safety-patches table layout parameters
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS player_cores (" +
                                "uuid TEXT PRIMARY KEY, " +
                                "equipped_core TEXT, " +
                                "trusted_players TEXT, " +
                                "actionbar_mode TEXT, " +
                                "active_cooldowns TEXT, " +
                                "passive_cooldowns TEXT)"
                );

                // Appends tracking parameters explicitly if table was built by older mod versions
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN active_cooldowns TEXT DEFAULT '{}'"); } catch (SQLException ignored) {}
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN passive_cooldowns TEXT DEFAULT '{}'"); } catch (SQLException ignored) {}
                try { statement.execute("ALTER TABLE player_cores ADD COLUMN actionbar_mode TEXT DEFAULT 'ICON'"); } catch (SQLException ignored) {}
            }
            AstralCores.LOGGER.info("AstralCores SQLite Database successfully loaded and healthy.");
        } catch (Exception e) {
            AstralCores.LOGGER.error("CRITICAL: Failed to initialize SQLite Database Engine!", e);
        }
    }

    // Loads persistent database records into active RAM cache on player join
    public void load(ServerPlayer player) {
        UUID uuid = player.getUUID();
        String query = "SELECT * FROM player_cores WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                PlayerData data = new PlayerData();

                if (rs.next()) {
                    String equipped = rs.getString("equipped_core");
                    String trustedJson = rs.getString("trusted_players");
                    String modeString = rs.getString("actionbar_mode");
                    String activeCooldownsJson = rs.getString("active_cooldowns");
                    String passiveCooldownsJson = rs.getString("passive_cooldowns");

                    // Reconstructs core status to Java Enum pattern
                    if (equipped != null) data.setEquippedCore(CoreType.valueOf(equipped));

                    // Converts trusted player text records back to active UUID objects
                    if (trustedJson != null && !trustedJson.isEmpty()) {
                        List<String> trustedStrings = gson.fromJson(trustedJson, listType);
                        if (trustedStrings != null) {
                            for (String tUuid : trustedStrings) {
                                data.addTrustedPlayer(UUID.fromString(tUuid));
                            }
                        }
                    }

                    // Converts text records back to native UI Enum structures safely
                    if (modeString != null) {
                        try {
                            data.setActionBarMode(ActionBarMode.valueOf(modeString));
                        } catch (IllegalArgumentException e) {
                            data.setActionBarMode(ActionBarMode.ICON);
                        }
                    }

                    // Restores active cooldown counters into running RAM registers
                    if (activeCooldownsJson != null && !activeCooldownsJson.isEmpty()) {
                        Map<CoreType, Integer> activeMap = gson.fromJson(activeCooldownsJson, cooldownMapType);
                        if (activeMap != null) data.getActiveCooldownsMap().putAll(activeMap);
                    }

                    // Restores passive cooldown counters into running RAM registers
                    if (passiveCooldownsJson != null && !passiveCooldownsJson.isEmpty()) {
                        Map<CoreType, Integer> passiveMap = gson.fromJson(passiveCooldownsJson, cooldownMapType);
                        if (passiveMap != null) data.getPassiveCooldownsMap().putAll(passiveMap);
                    }

                    AstralCores.LOGGER.info("Loaded SQLite profile data for player: {}", player.getScoreboardName());
                } else {
                    // Generates fresh database profile fields for primary validation cycles
                    insertNewPlayer(uuid);
                    AstralCores.LOGGER.info("Created brand new database profile row for player: {}", player.getScoreboardName());
                }

                cache.put(uuid, data);
            }
        } catch (SQLException e) {
            AstralCores.LOGGER.error("SQL Exception caught during profile load routine for: {}", uuid, e);
            cache.put(uuid, new PlayerData());
        }
    }
    // Fetches active RAM cached data profiles instantly for runtime checking
    public PlayerData get(ServerPlayer player) {
        PlayerData data = cache.get(player.getUUID());
        if (data == null) {
            throw new IllegalStateException("RAM cache missed runtime check for active player entity: " + player.getUUID());
        }
        return data;
    }

    // Serializes and commits active RAM cached parameters down into the database file
    public void save(ServerPlayer player) {
        UUID uuid = player.getUUID();
        PlayerData data = cache.get(uuid);
        if (data == null) return;

        String update = "UPDATE player_cores SET equipped_core = ?, trusted_players = ?, actionbar_mode = ?, active_cooldowns = ?, passive_cooldowns = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(update)) {
            // Converts the equipped core Enum type to a string value
            ps.setString(1, data.getEquippedCore() != null ? data.getEquippedCore().name() : null);

            // Serializes the list of trusted player UUIDs into a JSON string
            List<String> trustedStrings = new ArrayList<>();
            for (UUID tUuid : data.getTrustedPlayers()) {
                trustedStrings.add(tUuid.toString());
            }
            ps.setString(2, gson.toJson(trustedStrings));

            // Saves the action bar UI display mode configuration name
            ps.setString(3, data.getActionBarMode().name());

            // Serializes active talent capability cooldown counters into JSON text lines
            ps.setString(4, gson.toJson(data.getActiveCooldownsMap()));

            // Serializes passive talent property cooldown counters into JSON text lines
            ps.setString(5, gson.toJson(data.getPassiveCooldownsMap()));

            // Targets the specific player row using their string UUID index
            ps.setString(6, uuid.toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            AstralCores.LOGGER.error("Failed executing SQLite push operation during state save for: {}", uuid, e);
        }
    }

    // Saves current records to disk and removes the player profile from running RAM
    public void unload(ServerPlayer player) {
        save(player);
        cache.remove(player.getUUID());
    }

    // Inserts a blank starter data row format for a completely new player profile
    private void insertNewPlayer(UUID uuid) throws SQLException {
        String insert = "INSERT INTO player_cores (uuid, equipped_core, trusted_players, actionbar_mode, active_cooldowns, passive_cooldowns) VALUES (?, NULL, '[]', 'ICON', '{}', '{}')";
        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    // Closes the active database connection pool during server shutdown routines
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
