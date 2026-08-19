package de.ep.astralcores.structure;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;
import java.util.UUID;

public final class StructureManager {

    // Minimum horizontal distance allowed between structures
    private static final long MIN_STRUCTURE_DISTANCE = 200L;

    // Squared minimum distance used to avoid square root calculations
    private static final long MIN_STRUCTURE_DISTANCE_SQ =
            MIN_STRUCTURE_DISTANCE * MIN_STRUCTURE_DISTANCE;

    // Generates the configured amount of every valid structure type
    public static void serverStart(ServerLevel level) {
        StructureDataManager data = StructureDataManager.get(level);

        // Cache configuration values for this generation pass
        int amount = ConfigManager.get().general.structures_per_core;

        Identifier currentDimension = level.dimension().identifier();
        RandomSource random = createInitialRandom(level);

        for (StructureType type : StructureType.values()) {
            StructureDefinition definition = StructureRegistry.get(type);

            // Ignore structure types without a registered definition
            if (definition == null) {
                continue;
            }

            // Ignore structures that are not allowed in the current dimension
            if (!definition.allowedDimensions().isEmpty()
                    && !definition.allowedDimensions().contains(currentDimension)) {
                continue;
            }

            long existing = data.countActiveStructures(type);

            // Resolve the core once instead of once per spawned structure
            CoreType coreType = CoreToStructureLookup
                    .getCoreType(type)
                    .orElseThrow(() -> new IllegalStateException(
                            "Missing core type mapping for structure: " + type
                    ));

            Core core = CoreRegistry.get(coreType)
                    .orElseThrow(() -> new IllegalStateException(
                            "Failed to load core instance for type: " + coreType
                    ));

            while (existing < amount) {
                Optional<BlockPos> positionOpt = findValidPosition(
                        level,
                        definition,
                        data,
                        random,
                        null
                );

                // Stop if no valid position could be found
                if (positionOpt.isEmpty()) {
                    break;
                }

                BlockPos position = positionOpt.get();

                // Place the structure and spawn its core
                StructureSpawner.spawn(level, type, position, core);

                existing++;
            }
        }
    }

    // Searches for a valid position while respecting biome, radius and distance restrictions
    private static Optional<BlockPos> findValidPosition(
            ServerLevel level,
            StructureDefinition definition,
            StructureDataManager data,
            RandomSource random,
            BlockPos excludedPosition
    ) {
        // ConfigManager guarantees that structure_spawn_radius is at least 1500
        int configuredRadius =
                ConfigManager.get().general.structure_spawn_radius;

        // Keep the candidate origin 200 blocks inside the configured spawn radius
        int radius = configuredRadius - 200;

        // Use the world's configured spawn position as the search center
        BlockPos spawnPos = level.getRespawnData().pos();

        // Try up to 1000 random candidates before giving up
        for (int attempts = 0; attempts < 1000; attempts++) {

            int x = spawnPos.getX()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            int z = spawnPos.getZ()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            // Query the noise biome without generating the terrain chunk
            Holder<Biome> biome = level.getBiomeManager()
                    .getNoiseBiomeAtPosition(
                            x,
                            level.getMinY(),
                            z
                    );

            // Resolve the biome registry identifier
            Identifier currentBiome = biome.unwrapKey()
                    .map(ResourceKey::identifier)
                    .orElse(null);

            // Reject candidates outside the configured biome restrictions
            if (!definition.allowedBiomes().isEmpty()
                    && (currentBiome == null
                    || !definition.allowedBiomes().contains(currentBiome))) {
                continue;
            }

            // Reject replacement candidates too close to the old position
            if (excludedPosition != null
                    && horizontalDistanceSq(
                    excludedPosition,
                    x,
                    z
            ) < MIN_STRUCTURE_DISTANCE_SQ) {
                continue;
            }

            // Reject candidates too close to existing active structures
            boolean tooClose = data.getStructures()
                    .values()
                    .stream()
                    .filter(StructureDataManager.StructureInstance::active)
                    .anyMatch(instance ->
                            horizontalDistanceSq(
                                    instance.position(),
                                    x,
                                    z
                            ) < MIN_STRUCTURE_DISTANCE_SQ
                    );

            if (tooClose) {
                continue;
            }

            int y = level.getChunkSource()
                    .getGenerator()
                    .getBaseHeight(
                            x,
                            z,
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            level,
                            level.getChunkSource().randomState()
                    );

            BlockPos candidate = new BlockPos(x, y, z);

            return Optional.of(candidate);
        }

        return Optional.empty();
    }

    // Calculates horizontal X/Z distance squared without considering height
    private static long horizontalDistanceSq(
            BlockPos position,
            int x,
            int z
    ) {
        long dx = (long) position.getX() - x;
        long dz = (long) position.getZ() - z;

        return dx * dx + dz * dz;
    }

    // Creates the RNG used for initial structure generation
    private static RandomSource createInitialRandom(ServerLevel level) {
        // Random mode uses Minecraft's runtime random source
        if (ConfigManager.get().general.randomized_structure_spawn) {
            return level.getRandom();
        }

        // Deterministic mode derives the sequence only from the world seed
        return RandomSource.create(level.getSeed());
    }

    // Replaces a structure after its core has been removed or destroyed
    public static void onCoreRemoved(
            ServerLevel level,
            UUID uuid
    ) {
        StructureDataManager data = StructureDataManager.get(level);

        // Find the structure associated with the removed core
        StructureDataManager.StructureInstance instance =
                data.getStructure(uuid);

        if (instance == null) {
            return;
        }

        // Preserve the old position before deactivating the structure
        BlockPos oldPosition = instance.position();

        // Mark the destroyed structure as inactive
        data.deactivateStructureByUUID(uuid);

        StructureDefinition definition =
                StructureRegistry.get(instance.type());

        // Stop if the structure definition no longer exists
        if (definition == null) {
            return;
        }

        // Replacements use a fresh runtime random source
        RandomSource random = level.getRandom();

        findValidPosition(
                level,
                definition,
                data,
                random,
                oldPosition
        ).ifPresent(newPos -> {

            // Resolve the core associated with the destroyed structure
            Core core = CoreRegistry.get(instance.coreType())
                    .orElseThrow(() -> new IllegalStateException(
                            "Failed to load core instance for type: "
                                    + instance.coreType()
                    ));

            // Spawn the replacement at the newly selected position
            StructureSpawner.spawn(
                    level,
                    instance.type(),
                    newPos,
                    core
            );
        });
    }
}