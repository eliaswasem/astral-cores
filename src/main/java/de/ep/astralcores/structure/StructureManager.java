package de.ep.astralcores.structure;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.structure.spawners.MeteorSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class StructureManager {

    private static final long MIN_STRUCTURE_DISTANCE = 200L;
    private static final long MIN_STRUCTURE_DISTANCE_SQ = MIN_STRUCTURE_DISTANCE * MIN_STRUCTURE_DISTANCE;

    // Keeps pending spawns thread-safe and prevents chunk-loading deadlocks.
    private static final ConcurrentLinkedQueue<SpawnTask> spawnQueue = new ConcurrentLinkedQueue<>();

    private record SpawnTask(ServerLevel level, StructureDefinition definition, BlockPos pos, PlannedStructure planned) {}

    public static void serverStart(ServerLevel level) {
        StructureDataManager data = StructureDataManager.get(level);
        int amount = ConfigManager.get().structure.structures_per_core;
        Identifier currentDimension = level.dimension().identifier();
        RandomSource random = createInitialRandom(level);

        for (StructureType type : StructureType.values()) {
            StructureDefinition definition = StructureRegistry.get(type);
            if (definition == null) continue;

            if (!definition.allowedDimensions().isEmpty() && !definition.allowedDimensions().contains(currentDimension)) {
                continue;
            }

            long existing = data.countLinkedStructures(type) + data.countPlannedStructures(type);

            while (existing < amount) {
                Optional<BlockPos> positionOpt = findValidPosition(level, definition, data, random, null);
                if (positionOpt.isEmpty()) break;

                BlockPos position = positionOpt.get();
                data.addPlannedStructure(type, position);
                existing++;
            }
        }
    }

    private static Optional<BlockPos> findValidPosition(
            ServerLevel level,
            StructureDefinition definition,
            StructureDataManager data,
            RandomSource random,
            BlockPos excludedPosition
    ) {
        int configuredRadius = ConfigManager.get().structure.structure_spawn_radius;
        int radius = configuredRadius - 200;
        BlockPos spawnPos = level.getRespawnData().pos();

        for (int attempts = 0; attempts < 1000; attempts++) {
            int x = spawnPos.getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = spawnPos.getZ() + random.nextInt(radius * 2 + 1) - radius;

            boolean tooClose = data.getAllStructurePositions().stream()
                    .anyMatch(pos -> horizontalDistanceSq(pos, x, z) < MIN_STRUCTURE_DISTANCE_SQ);

            if (tooClose) continue;

            Holder<Biome> preBiome = level.getBiomeManager().getNoiseBiomeAtPosition(x, 64, z);
            Identifier preBiomeId = preBiome.unwrapKey().map(ResourceKey::identifier).orElse(null);

            if (!definition.allowedBiomes().isEmpty() && (preBiomeId == null || !definition.allowedBiomes().contains(preBiomeId))) {
                continue;
            }

            if (excludedPosition != null && horizontalDistanceSq(excludedPosition, x, z) < MIN_STRUCTURE_DISTANCE_SQ) {
                continue;
            }

            // Calculates terrain height mathematically without loading the target chunk.
            int y = level.getChunkSource().getGenerator().getBaseHeight(
                    x,
                    z,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    level,
                    level.getChunkSource().randomState()
            );

            Holder<Biome> finalBiome = level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z);
            Identifier finalBiomeId = finalBiome.unwrapKey().map(ResourceKey::identifier).orElse(null);

            if (!definition.allowedBiomes().isEmpty() && (finalBiomeId == null || !definition.allowedBiomes().contains(finalBiomeId))) {
                continue;
            }

            return Optional.of(new BlockPos(x, y, z));
        }
        return Optional.empty();
    }

    public static void onChunkLoad(ServerLevel level, ChunkAccess chunk) {
        StructureDataManager data = StructureDataManager.get(level);
        ChunkPos chunkPos = chunk.getPos();

        List<PlannedStructure> plannedList = data.getPlannedStructuresInChunk(chunkPos.x(), chunkPos.z());
        if (plannedList.isEmpty()) return;

        for (PlannedStructure planned : plannedList) {
            StructureDefinition definition = StructureRegistry.get(planned.type());
            if (definition == null) continue;

            int realX = planned.position().getX();
            int realZ = planned.position().getZ();
            int realY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, realX & 15, realZ & 15);

            BlockPos finalSpawnPos = new BlockPos(realX, realY, realZ);

            // Queues the spawn to avoid performing structure generation during chunk loading.
            spawnQueue.add(new SpawnTask(level, definition, finalSpawnPos, planned));
        }
    }

    public static void tick(MinecraftServer server) {
        if (spawnQueue.isEmpty()) return;

        int processedThisTick = 0;
        int attempts = 0;
        int maxAttempts = spawnQueue.size(); // Limits processing to the tasks present when this tick started.

        // Processes at most two successful spawns while checking each queued task only once.
        while (!spawnQueue.isEmpty() && processedThisTick < 2 && attempts < maxAttempts) {
            SpawnTask task = spawnQueue.poll();
            if (task == null) continue;

            attempts++;
            ChunkPos targetChunk = new ChunkPos(task.pos().getX() >> 4, task.pos().getZ() >> 4);

            // Ensures the target chunk is currently loaded before spawning.
            if (task.level().getChunkSource().hasChunk(targetChunk.x(), targetChunk.z())) {
                MeteorSpawner.spawn(task.level(), task.definition(), task.pos());

                StructureDataManager data = StructureDataManager.get(task.level());
                data.convertPlannedToActive(task.planned(), task.pos());

                processedThisTick++;
            } else {
                // Requeues unloaded chunks so they can be processed on a later tick.
                spawnQueue.add(task);
            }
        }
    }


    public static boolean onCoreRemoved(ServerLevel level, UUID coreUuid) {
        StructureDataManager data = StructureDataManager.get(level);
        StructureInstance instance = data.getStructure(coreUuid);
        if (instance == null) return false;

        BlockPos oldPosition = instance.position();
        if (!data.delinkStructureByUUID(coreUuid)) return false;

        StructureDefinition definition = StructureRegistry.get(instance.type());
        if (definition == null) return false;

        Optional<BlockPos> newPosition = findValidPosition(level, definition, data, level.getRandom(), oldPosition);
        if (newPosition.isEmpty()) return false;

        data.addPlannedStructure(instance.type(), newPosition.get());
        return true;
    }

    private static long horizontalDistanceSq(BlockPos position, int x, int z) {
        long dx = (long) position.getX() - x;
        long dz = (long) position.getZ() - z;
        return dx * dx + dz * dz;
    }

    private static RandomSource createInitialRandom(ServerLevel level) {
        if (ConfigManager.get().structure.randomized_structure_spawn) {
            return level.getRandom();
        }
        return RandomSource.create(level.getSeed());
    }
}