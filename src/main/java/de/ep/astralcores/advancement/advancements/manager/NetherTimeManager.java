package de.ep.astralcores.advancement.advancements.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NetherTimeManager {

    private static final long REQUIRED_TIME =
            //12h
           12L * 60L * 60L * 1000L;

    private static final Map<UUID, Long> NETHER_TIME =
            new HashMap<>();


    private static final Map<UUID, Long> LAST_START =
            new HashMap<>();


    private static final Map<UUID, ServerBossEvent> BOSS_BARS =
            new HashMap<>();


    private static final Set<UUID> COMPLETED =
            new HashSet<>();

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static Path saveFile;

    private static boolean initialized = false;


    public static void init(MinecraftServer server) {

        if (initialized) {
            return;
        }

        initialized = true;

        saveFile = server.getServerDirectory()
                .resolve("astralcores_nether_time.json");

        load();
    }



    public static void tick(MinecraftServer server) {

        init(server);

        long now = System.currentTimeMillis();

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            UUID uuid = player.getUUID();


            if (COMPLETED.contains(uuid)) {

                ServerBossEvent bossBar =
                        BOSS_BARS.get(uuid);

                if (bossBar != null) {
                    bossBar.removePlayer(player);
                }

                continue;
            }


            if (player.level().dimension() != Level.NETHER) {

                pausePlayer(uuid, now);

                ServerBossEvent bossBar =
                        BOSS_BARS.get(uuid);

                if (bossBar != null) {
                    bossBar.removePlayer(player);
                }

                continue;
            }


            startPlayer(uuid, now);


            long elapsed =
                    getElapsedTime(uuid, now);


            ServerBossEvent bossBar =
                    BOSS_BARS.computeIfAbsent(
                            uuid,
                            id -> createBossBar()
                    );

            bossBar.addPlayer(player);

            if (elapsed < REQUIRED_TIME) {

                bossBar.setProgress(
                        calculateProgress(elapsed)
                );

                bossBar.setName(
                        createBossBarName(elapsed)
                );

                continue;
            }


            NETHER_TIME.put(
                    uuid,
                    REQUIRED_TIME
            );

            LAST_START.remove(uuid);

            save();


            CriterionRegistry.NETHER_TIME.trigger(
                    player,
                    REQUIRED_TIME
            );



            COMPLETED.add(uuid);

            save();

            removeBossBar(uuid);
        }
    }


    private static void startPlayer(
            UUID uuid,
            long now
    ) {

        LAST_START.putIfAbsent(
                uuid,
                now
        );
    }


    private static void pausePlayer(
            UUID uuid,
            long now
    ) {

        Long start =
                LAST_START.remove(uuid);

        if (start == null) {
            return;
        }

        long elapsed =
                Math.max(
                        0L,
                        now - start
                );

        long current =
                NETHER_TIME.getOrDefault(
                        uuid,
                        0L
                );

        long total =
                Math.min(
                        REQUIRED_TIME,
                        current + elapsed
                );

        NETHER_TIME.put(
                uuid,
                total
        );

        save();
    }

    private static long getElapsedTime(
            UUID uuid,
            long now
    ) {

        long stored =
                NETHER_TIME.getOrDefault(
                        uuid,
                        0L
                );

        long start =
                LAST_START.getOrDefault(
                        uuid,
                        now
                );

        return Math.min(
                REQUIRED_TIME,
                stored +
                        Math.max(
                                0L,
                                now - start
                        )
        );
    }


    private static ServerBossEvent createBossBar() {

        return new ServerBossEvent(
                UUID.randomUUID(),
                Component.literal("Nether Time"),
                ServerBossEvent.BossBarColor.RED,
                ServerBossEvent.BossBarOverlay.PROGRESS
        );
    }


    private static float calculateProgress(
            long elapsed
    ) {

        return Math.clamp(
                1.0F -
                        (float) elapsed /
                                REQUIRED_TIME,
                0.0F,
                1.0F
        );
    }


    private static Component createBossBarName(
            long elapsed
    ) {

        long remaining =
                Math.max(
                        0L,
                        REQUIRED_TIME - elapsed
                );

        long totalSeconds =
                remaining / 1000L;

        long hours =
                totalSeconds / 3600L;

        long minutes =
                (totalSeconds % 3600L) / 60L;

        long seconds =
                totalSeconds % 60L;

        return Component.literal(
                "Nether Time: " +
                        String.format(
                                "%02d:%02d:%02d",
                                hours,
                                minutes,
                                seconds
                        )
        );
    }


    private static void removeBossBar(
            UUID uuid
    ) {

        ServerBossEvent bossBar =
                BOSS_BARS.remove(uuid);

        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }
    }


    public static void removePlayer(
            ServerPlayer player
    ) {

        long now =
                System.currentTimeMillis();

        UUID uuid =
                player.getUUID();

        pausePlayer(
                uuid,
                now
        );

        removeBossBar(uuid);
    }


    public static void save() {

        if (saveFile == null) {
            return;
        }

        try {

            try (Writer writer =
                         Files.newBufferedWriter(saveFile)) {

                GSON.toJson(
                        NETHER_TIME,
                        writer
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private static void load() {

        if (saveFile == null ||
                !Files.exists(saveFile)) {

            return;
        }

        try {

            Type type =
                    new TypeToken<Map<UUID, Long>>() {}
                            .getType();

            try (Reader reader =
                         Files.newBufferedReader(saveFile)) {

                Map<UUID, Long> loaded =
                        GSON.fromJson(
                                reader,
                                type
                        );

                if (loaded != null) {

                    NETHER_TIME.putAll(
                            loaded
                    );

                    for (Map.Entry<UUID, Long> entry :
                            loaded.entrySet()) {

                        if (entry.getValue() >= REQUIRED_TIME) {
                            COMPLETED.add(entry.getKey());
                        }
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void onServerStopping(
            MinecraftServer server
    ) {

        long now =
                System.currentTimeMillis();


        for (UUID uuid :
                new HashSet<>(LAST_START.keySet())) {

            pausePlayer(
                    uuid,
                    now
            );
        }


        save();

        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();


        LAST_START.clear();


        initialized = false;
    }


    public static void clear() {

        NETHER_TIME.clear();
        LAST_START.clear();
        COMPLETED.clear();


        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();

        save();
    }
}