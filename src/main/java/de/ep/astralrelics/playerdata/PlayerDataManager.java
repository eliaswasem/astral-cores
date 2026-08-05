package de.ep.astralrelics.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.ep.astralrelics.AstralRelics;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PlayerDataManager {


    private final File folder;


    private final Map<UUID, PlayerData> cache = new HashMap<>();


    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();



    public PlayerDataManager(File worldFolder) {

        this.folder = new File(
                worldFolder,
                "astralrelics/playerdata"
        );


        if (!folder.exists()) {

            if (folder.mkdirs()) {

                AstralRelics.LOGGER.info(
                        "Created player data folder"
                );

            } else {

                AstralRelics.LOGGER.error(
                        "Failed to create player data folder"
                );

            }

        }

    }



    /*
     * Loads player data from disk when a player joins.
     */
    public void load(ServerPlayer player) {

        UUID uuid = player.getUUID();

        File file = getFile(uuid);


        if (!file.exists()) {

            PlayerData data = new PlayerData();

            cache.put(uuid, data);

            save(player);

            AstralRelics.LOGGER.info(
                    "Created new player data for {}",
                    uuid
            );

            return;
        }



        try (FileReader reader = new FileReader(file)) {

            PlayerData data =
                    gson.fromJson(reader, PlayerData.class);


            if (data == null) {
                data = new PlayerData();
            }


            cache.put(uuid, data);


            AstralRelics.LOGGER.info(
                    "Loaded player data for {}",
                    uuid
            );


        } catch (Exception e) {

            cache.put(
                    uuid,
                    new PlayerData()
            );


            AstralRelics.LOGGER.error(
                    "Failed loading player data for {}",
                    uuid,
                    e
            );

        }

    }



    /*
     * Returns the player data stored in memory.
     */
    public PlayerData get(ServerPlayer player) {

        PlayerData data =
                cache.get(player.getUUID());


        if (data == null) {

            throw new IllegalStateException(
                    "Player data not loaded for " + player.getUUID()
            );

        }


        return data;

    }



    /*
     * Saves player data from memory to disk.
     */
    public void save(ServerPlayer player) {

        UUID uuid = player.getUUID();

        PlayerData data = cache.get(uuid);


        if (data == null) {
            return;
        }


        try (FileWriter writer = new FileWriter(getFile(uuid))) {

            gson.toJson(data, writer);


        } catch (IOException e) {

            AstralRelics.LOGGER.error(
                    "Failed saving player data for {}",
                    uuid,
                    e
            );

        }

    }



    /*
     * Saves player data and removes it from memory.
     */
    public void unload(ServerPlayer player) {

        save(player);

        cache.remove(
                player.getUUID()
        );

    }



    private File getFile(UUID uuid) {

        return new File(
                folder,
                uuid + ".json"
        );

    }

}
