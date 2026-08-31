package de.ep.astralcores.core.respawn;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.respawn.data.CoreRespawnData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Handles active core respawn timers and their boss bars
public class CoreRespawnManager {

    // Maps every individual respawn timer to its boss bar
    private static final Map<
            CoreRespawnData,
            ServerBossEvent
            > BOSS_BARS =
            new IdentityHashMap<>();

    // Prevents unnecessary boss bar updates more than once per second
    private static long lastBossBarUpdate;

    // Adds a new respawn timer for a core
    public static void addRespawn(
            CoreType type
    ) {
        Core core =
                CoreRegistry
                        .get(type)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No Core registered for type: "
                                                        + type
                                        )
                        );

        long startTimestamp =
                System.currentTimeMillis() / 1000L;

        long endTimestamp =
                startTimestamp
                        + core.getRespawnDuration();

        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (data == null) {
            return;
        }

        data.addRespawn(
                type,
                startTimestamp,
                endTimestamp
        );
    }

    // Checks all respawn timers every server tick
    public static void tick() {

        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (data == null) {
            return;
        }

        long now =
                System.currentTimeMillis() / 1000L;

        List<CoreRespawnData> respawns =
                new ArrayList<>(
                        data.getAllRespawns()
                );

        respawns.sort(
                Comparator.comparingLong(
                        CoreRespawnData::endTimestamp
                )
        );

        for (CoreRespawnData respawn :
                respawns) {

            // Respawn timer has finished
            if (respawn.endTimestamp() <= now) {

                spawnCore(
                        respawn.type()
                );

                data.removeRespawn(
                        respawn
                );

                removeBossBar(
                        respawn
                );

                continue;
            }

            // Timer has not started yet
            if (now < respawn.startTimestamp()) {
                continue;
            }

            ServerBossEvent bossBar =
                    BOSS_BARS.computeIfAbsent(
                            respawn,
                            CoreRespawnManager::createBossBar
                    );

            // Only update boss bars once per second
            if (now != lastBossBarUpdate) {

                bossBar.setProgress(
                        calculateProgress(
                                respawn,
                                now
                        )
                );

                bossBar.setName(
                        createBossBarName(
                                respawn,
                                now
                        )
                );
            }
        }

        lastBossBarUpdate = now;

        cleanupBossBars(
                respawns
        );
    }

    // Creates a boss bar for an individual respawn timer
    private static ServerBossEvent createBossBar(
            CoreRespawnData respawn
    ) {
        Core core =
                CoreRegistry
                        .get(respawn.type())
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No Core registered for type: "
                                                        + respawn.type()
                                        )
                        );

        ServerBossEvent bossBar =
                new ServerBossEvent(
                        UUID.randomUUID(),
                        createBossBarName(
                                respawn,
                                System.currentTimeMillis() / 1000L
                        ),
                        core.getBossBarColor(),
                        ServerBossEvent.BossBarOverlay.PROGRESS
                );

        MinecraftServer server =
                AstralCores.getServer();

        if (server == null) {
            return bossBar;
        }

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            bossBar.addPlayer(player);
        }

        return bossBar;
    }

    // Removes the boss bar belonging to a completed respawn
    private static void removeBossBar(
            CoreRespawnData respawn
    ) {
        ServerBossEvent bossBar =
                BOSS_BARS.remove(respawn);

        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }
    }

    // Removes boss bars for timers that no longer exist
    private static void cleanupBossBars(
            List<CoreRespawnData> respawns
    ) {
        BOSS_BARS.entrySet()
                .removeIf(
                        entry -> {

                            if (respawns.contains(
                                    entry.getKey()
                            )) {
                                return false;
                            }

                            entry.getValue()
                                    .removeAllPlayers();

                            return true;
                        }
                );
    }

    // Calculates the remaining boss bar progress
    private static float calculateProgress(
            CoreRespawnData respawn,
            long now
    ) {
        long start =
                respawn.startTimestamp();

        long end =
                respawn.endTimestamp();

        long duration =
                end - start;

        if (duration <= 0) {
            return 0.0F;
        }

        long remaining =
                end - now;

        return Math.clamp(
                (float) remaining / duration,
                0.0F,
                1.0F
        );
    }

    // Creates the text displayed by the boss bar
    private static Component createBossBarName(
            CoreRespawnData respawn,
            long now
    ) {
        long remaining =
                Math.max(
                        0L,
                        respawn.endTimestamp() - now
                );

        Core core =
                CoreRegistry
                        .get(respawn.type())
                        .orElseThrow();

        return Component.literal(
                core.getName()
                        + " respawning in "
                        + formatTime(remaining)
        );
    }

    // Formats remaining seconds into a readable time string
    private static String formatTime(
            long seconds
    ) {
        long days =
                seconds / 86400L;

        seconds %= 86400L;

        long hours =
                seconds / 3600L;

        seconds %= 3600L;

        long minutes =
                seconds / 60L;

        seconds %= 60L;

        StringBuilder result =
                new StringBuilder();

        if (days != 0) {
            result.append(days)
                    .append("d ");
        }

        if (hours != 0) {
            result.append(hours)
                    .append("h ");
        }

        if (minutes != 0) {
            result.append(minutes)
                    .append("m ");
        }

        if (seconds != 0 || result.isEmpty()) {
            result.append(seconds)
                    .append("s");
        }

        return result.toString().trim();
    }

    // Spawns the core when its respawn timer finishes
    private static void spawnCore(
            CoreType type
    ) {
        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (!data.altarExists()) {
            AstralCores.LOGGER.warn(
                    "Cannot respawn {}: no altar exists.",
                    type.name()
            );

            return;
        }

        AstralCores.LOGGER.info(
                "Respawning core: {}",
                type.name()
        );

        // TODO: Spawn the core at the altar respawn position
    }

    // Adds a newly joined player to every active boss bar
    public static void addPlayer(
            ServerPlayer player
    ) {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.addPlayer(player);
        }
    }

    // Removes a disconnected player from every active boss bar
    public static void removePlayer(
            ServerPlayer player
    ) {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removePlayer(player);
        }
    }

    // Removes all active boss bars
    public static void clear() {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();
    }
}