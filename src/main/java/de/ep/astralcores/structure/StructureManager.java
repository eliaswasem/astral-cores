package de.ep.astralcores.structure;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.structure.spawners.MeteorSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// Coordinates structure planning, chunk preparation and structure spawning.
public final class StructureManager {

    private static final long MIN_STRUCTURE_DISTANCE = 200L;
    private static final long MIN_STRUCTURE_DISTANCE_SQ =
            MIN_STRUCTURE_DISTANCE * MIN_STRUCTURE_DISTANCE;

    private static final int MAX_PLANNING_ATTEMPTS_PER_TICK = 5;
    private static final int MAX_SPAWN_TASKS_PER_TICK = 5;
    private static final int MAX_POSITION_ATTEMPTS = 1000;

    // Keeps pending structure planning thread-safe and prevents long server ticks.
    private static final ConcurrentLinkedQueue<PlanningTask> planningQueue =
            new ConcurrentLinkedQueue<>();

    // Keeps pending spawns thread-safe and prevents chunk-loading deadlocks.
    private static final ConcurrentLinkedQueue<SpawnTask> spawnQueue =
            new ConcurrentLinkedQueue<>();

    // Prevents the same planned structure from being queued multiple times.
    private static final Set<PlannedStructure> queuedStructures =
            ConcurrentHashMap.newKeySet();

    private record PlanningTask(
            ServerLevel level,
            StructureType type,
            StructureDefinition definition,
            StructureDataManager data,
            RandomSource random,
            int attempts
    ) {
    }

    private record SpawnTask(
            ServerLevel level,
            StructureDefinition definition,
            StructureTemplate template,
            BlockPos pos,
            PlannedStructure planned,
            Set<ChunkPos> requiredChunks
    ) {
    }

    public static void serverStart(ServerLevel level) {
        StructureDataManager data =
                StructureDataManager.get(level);

        int amount =
                ConfigManager.get()
                        .structure
                        .structures_per_core;

        Identifier currentDimension =
                level.dimension().identifier();

        RandomSource random =
                createInitialRandom(level);

        for (StructureType type : StructureType.values()) {

            StructureDefinition definition =
                    StructureRegistry.get(type);

            if (definition == null) {
                continue;
            }

            if (!definition.allowedDimensions().isEmpty()
                    && !definition.allowedDimensions()
                    .contains(currentDimension)) {
                continue;
            }

            long existing =
                    data.countLinkedStructures(type)
                            + data.countPlannedStructures(type);

            if (existing >= amount) {
                continue;
            }

            /*
             * Planning is deliberately deferred to tick().
             * serverStart() must not perform potentially thousands of
             * terrain and biome checks synchronously.
             */
            planningQueue.add(
                    new PlanningTask(
                            level,
                            type,
                            definition,
                            data,
                            random,
                            0
                    )
            );
        }
    }

    private static void processPlanning() {
        int processed =
                0;

        /*
         * Performs only a small number of candidate checks per tick.
         * This prevents serverStart from creating a large synchronous
         * workload on the server thread.
         */
        while (!planningQueue.isEmpty()
                && processed < MAX_PLANNING_ATTEMPTS_PER_TICK) {

            PlanningTask task =
                    planningQueue.poll();

            if (task == null) {
                continue;
            }

            StructureDataManager data =
                    task.data();

            int amount =
                    ConfigManager.get()
                            .structure
                            .structures_per_core;

            long existing =
                    data.countLinkedStructures(task.type())
                            + data.countPlannedStructures(task.type());

            if (existing >= amount) {
                continue;
            }

            if (task.attempts() >= MAX_POSITION_ATTEMPTS) {
                continue;
            }

            processed++;

            Optional<BlockPos> positionOpt =
                    findValidPosition(
                            task.level(),
                            task.definition(),
                            data,
                            task.random(),
                            null
                    );

            if (positionOpt.isPresent()) {

                data.addPlannedStructure(
                        task.type(),
                        positionOpt.get()
                );

                existing++;
            }

            /*
             * Keep planning this structure type until the configured
             * amount has been reached or the maximum number of position
             * attempts has been exhausted.
             */
            if (existing < amount
                    && task.attempts() + 1 < MAX_POSITION_ATTEMPTS) {

                planningQueue.add(
                        new PlanningTask(
                                task.level(),
                                task.type(),
                                task.definition(),
                                data,
                                task.random(),
                                task.attempts() + 1
                        )
                );
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
        int configuredRadius =
                ConfigManager.get()
                        .structure
                        .structure_spawn_radius;

        int radius =
                configuredRadius - 200;

        BlockPos spawnPos =
                level.getRespawnData().pos();

        int x =
                spawnPos.getX()
                        + random.nextInt(radius * 2 + 1)
                        - radius;

        int z =
                spawnPos.getZ()
                        + random.nextInt(radius * 2 + 1)
                        - radius;

        boolean tooClose =
                data.getAllStructurePositions()
                        .stream()
                        .anyMatch(
                                pos ->
                                        horizontalDistanceSq(
                                                pos,
                                                x,
                                                z
                                        ) < MIN_STRUCTURE_DISTANCE_SQ
                        );

        if (tooClose) {
            return Optional.empty();
        }

        if (excludedPosition != null
                && horizontalDistanceSq(
                excludedPosition,
                x,
                z
        ) < MIN_STRUCTURE_DISTANCE_SQ) {
            return Optional.empty();
        }

        // Calculates terrain height mathematically without loading the target chunk.
        int y =
                level.getChunkSource()
                        .getGenerator()
                        .getBaseHeight(
                                x,
                                z,
                                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                level,
                                level.getChunkSource()
                                        .randomState()
                        );

        Holder<Biome> finalBiome =
                level.getBiomeManager()
                        .getNoiseBiomeAtPosition(
                                x,
                                y,
                                z
                        );

        Identifier finalBiomeId =
                finalBiome.unwrapKey()
                        .map(ResourceKey::identifier)
                        .orElse(null);

        if (!definition.allowedBiomes().isEmpty()
                && (finalBiomeId == null
                || !definition.allowedBiomes()
                .contains(finalBiomeId))) {
            return Optional.empty();
        }

        return Optional.of(
                new BlockPos(
                        x,
                        y,
                        z
                )
        );
    }

    public static void onChunkLoad(
            ServerLevel level,
            ChunkAccess chunk
    ) {
        StructureDataManager data =
                StructureDataManager.get(level);

        ChunkPos chunkPos =
                chunk.getPos();

        List<PlannedStructure> plannedList =
                data.getPlannedStructuresInChunk(
                        chunkPos.x(),
                        chunkPos.z()
                );

        if (plannedList.isEmpty()) {
            return;
        }

        for (PlannedStructure planned : plannedList) {
            queuePlannedStructure(
                    level,
                    chunk,
                    planned
            );
        }
    }

    private static void queuePlannedStructure(
            ServerLevel level,
            ChunkAccess chunk,
            PlannedStructure planned
    ) {
        // Prevents duplicate runtime tasks for the same planned structure.
        if (!queuedStructures.add(planned)) {
            return;
        }

        StructureDefinition definition =
                StructureRegistry.get(
                        planned.type()
                );

        if (definition == null) {
            queuedStructures.remove(planned);
            return;
        }

        Optional<StructureTemplate> templateOpt =
                TemplateManager.get(
                        level,
                        definition
                );

        if (templateOpt.isEmpty()) {
            queuedStructures.remove(planned);
            return;
        }

        StructureTemplate template =
                templateOpt.get();

        int realX =
                planned.position().getX();

        int realZ =
                planned.position().getZ();

        /*
         * Uses the ChunkAccess which is already loaded.
         * Never call level.getHeight() here because that can synchronously
         * request another chunk while Minecraft is processing a chunk load.
         */
        int realY =
                chunk.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        realX & 15,
                        realZ & 15
                );

        BlockPos finalSpawnPos =
                new BlockPos(
                        realX,
                        realY,
                        realZ
                );

        // Calculates the exact world-space bounds using the same placement settings as the placer.
        BoundingBox boundingBox =
                TemplateManager.getBoundingBox(
                        template,
                        finalSpawnPos
                );

        Set<ChunkPos> requiredChunks =
                calculateRequiredChunks(
                        boundingBox
                );

        // Queues the spawn to avoid performing structure generation during chunk loading.
        spawnQueue.add(
                new SpawnTask(
                        level,
                        definition,
                        template,
                        finalSpawnPos,
                        planned,
                        requiredChunks
                )
        );
    }

    /*
     * Queues a planned structure whose target chunk is not necessarily loaded.
     * This path uses the position already stored in the plan.
     */
    private static void queuePlannedStructure(
            ServerLevel level,
            PlannedStructure planned
    ) {
        // Prevents duplicate runtime tasks for the same planned structure.
        if (!queuedStructures.add(planned)) {
            return;
        }

        StructureDefinition definition =
                StructureRegistry.get(
                        planned.type()
                );

        if (definition == null) {
            queuedStructures.remove(planned);
            return;
        }

        Optional<StructureTemplate> templateOpt =
                TemplateManager.get(
                        level,
                        definition
                );

        if (templateOpt.isEmpty()) {
            queuedStructures.remove(planned);
            return;
        }

        StructureTemplate template =
                templateOpt.get();

        BlockPos finalSpawnPos =
                planned.position();

        // Calculates the exact world-space bounds using the same placement settings as the placer.
        BoundingBox boundingBox =
                TemplateManager.getBoundingBox(
                        template,
                        finalSpawnPos
                );

        Set<ChunkPos> requiredChunks =
                calculateRequiredChunks(
                        boundingBox
                );

        spawnQueue.add(
                new SpawnTask(
                        level,
                        definition,
                        template,
                        finalSpawnPos,
                        planned,
                        requiredChunks
                )
        );
    }

    public static void tick(
            MinecraftServer server
    ) {
        /*
         * Structure planning and actual structure spawning have separate
         * budgets so neither operation can monopolize the server tick.
         */
        processPlanning();
        processSpawning();
    }

    private static void processSpawning() {
        if (spawnQueue.isEmpty()) {
            return;
        }

        int processed =
                0;

        // Processes at most five structure tasks per tick.
        while (!spawnQueue.isEmpty()
                && processed < MAX_SPAWN_TASKS_PER_TICK) {

            SpawnTask task =
                    spawnQueue.poll();

            if (task == null) {
                continue;
            }

            processed++;

            // Requests every chunk required by the structure.
            if (!ensureRequiredChunksLoaded(task)) {
                // Requeues the structure until all required chunks are loaded.
                spawnQueue.add(task);
                continue;
            }

            StructureSpawnResult result =
                    MeteorSpawner.spawn(
                            task.level(),
                            task.definition(),
                            task.pos(),
                            task.template()
                    );

            StructureDataManager data =
                    StructureDataManager.get(
                            task.level()
                    );

            data.convertPlannedToActive(
                    task.planned(),
                    result.origin(),
                    result.coreUuid()
            );

            releaseChunkTickets(task);

            queuedStructures.remove(
                    task.planned()
            );
        }
    }

    private static boolean ensureRequiredChunksLoaded(
            SpawnTask task
    ) {
        ServerChunkCache chunkSource =
                task.level().getChunkSource();

        boolean allChunksLoaded = true;

        for (ChunkPos chunkPos :
                task.requiredChunks()) {

            if (chunkSource.hasChunk(
                    chunkPos.x(),
                    chunkPos.z()
            )) {
                continue;
            }

            // Requests the chunk without synchronously loading it during this tick.
            chunkSource.addTicketWithRadius(
                    TicketType.FORCED,
                    chunkPos,
                    0
            );

            allChunksLoaded = false;
        }

        return allChunksLoaded;
    }

    private static Set<ChunkPos> calculateRequiredChunks(
            BoundingBox boundingBox
    ) {
        int minChunkX =
                boundingBox.minX() >> 4;

        int minChunkZ =
                boundingBox.minZ() >> 4;

        int maxChunkX =
                boundingBox.maxX() >> 4;

        int maxChunkZ =
                boundingBox.maxZ() >> 4;

        Set<ChunkPos> chunks =
                new HashSet<>();

        for (int chunkX = minChunkX;
             chunkX <= maxChunkX;
             chunkX++) {

            for (int chunkZ = minChunkZ;
                 chunkZ <= maxChunkZ;
                 chunkZ++) {

                chunks.add(
                        new ChunkPos(
                                chunkX,
                                chunkZ
                        )
                );
            }
        }

        return chunks;
    }

    private static void releaseChunkTickets(
            SpawnTask task
    ) {
        ServerChunkCache chunkSource =
                task.level().getChunkSource();

        for (ChunkPos chunkPos :
                task.requiredChunks()) {

            chunkSource.removeTicketWithRadius(
                    TicketType.FORCED,
                    chunkPos,
                    0
            );
        }
    }

    public static boolean onCoreRemoved(
            ServerLevel level,
            UUID coreUuid
    ) {
        StructureDataManager data =
                StructureDataManager.get(level);

        StructureInstance instance =
                data.getStructure(coreUuid);

        if (instance == null) {
            return false;
        }

        BlockPos oldPosition =
                instance.position();

        if (!data.delinkStructureByUUID(coreUuid)) {
            return false;
        }

        StructureDefinition definition =
                StructureRegistry.get(
                        instance.type()
                );

        if (definition == null) {
            return false;
        }

        Optional<BlockPos> newPosition =
                findValidPosition(
                        level,
                        definition,
                        data,
                        level.getRandom(),
                        oldPosition
                );

        if (newPosition.isEmpty()) {
            /*
             * The search is intentionally only one candidate here.
             * Core removal itself is not allowed to block the server.
             */
            return false;
        }

        BlockPos position =
                newPosition.get();

        PlannedStructure planned =
                new PlannedStructure(
                        instance.type(),
                        position
                );

        data.addPlannedStructure(
                instance.type(),
                position
        );

        // Uses the same asynchronous placement pipeline without requiring the target chunk to be loaded.
        queuePlannedStructure(
                level,
                planned
        );

        return true;
    }

    private static long horizontalDistanceSq(
            BlockPos position,
            int x,
            int z
    ) {
        long dx =
                (long) position.getX() - x;

        long dz =
                (long) position.getZ() - z;

        return dx * dx + dz * dz;
    }

    private static RandomSource createInitialRandom(
            ServerLevel level
    ) {
        if (ConfigManager.get()
                .structure
                .randomized_structure_spawn) {

            return level.getRandom();
        }

        return RandomSource.create(
                level.getSeed()
        );
    }
}