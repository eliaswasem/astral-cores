package de.ep.astralcores.structure;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.structure.spawners.MeteorSpawner;
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

    private static final long MIN_STRUCTURE_DISTANCE = 200L;

    private static final long MIN_STRUCTURE_DISTANCE_SQ =
            MIN_STRUCTURE_DISTANCE * MIN_STRUCTURE_DISTANCE;

    public static void serverStart(ServerLevel level) {
        StructureDataManager data = StructureDataManager.get(level);

        int amount = ConfigManager.get().structure.structures_per_core;

        Identifier currentDimension = level.dimension().identifier();
        RandomSource random = createInitialRandom(level);

        for (StructureType type : StructureType.values()) {
            StructureDefinition definition = StructureRegistry.get(type);

            if (definition == null) {
                continue;
            }

            if (!definition.allowedDimensions().isEmpty()
                    && !definition.allowedDimensions().contains(currentDimension)) {
                continue;
            }

            long existing = data.countLinkedStructures(type);

            while (existing < amount) {
                Optional<BlockPos> positionOpt = findValidPosition(
                        level,
                        definition,
                        data,
                        random,
                        null
                );

                if (positionOpt.isEmpty()) {
                    break;
                }

                BlockPos position = positionOpt.get();

                MeteorSpawner.spawn(
                        level,
                        definition,
                        position
                );

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
                ConfigManager.get().structure.structure_spawn_radius;

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
                    .filter(StructureInstance::hasLinkedCore)
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
        if (ConfigManager.get().structure.randomized_structure_spawn) {
            return level.getRandom();
        }

        // Deterministic mode derives the sequence only from the world seed
        return RandomSource.create(level.getSeed());
    }

    // Replaces a structure after its core has been removed or destroyed
    public static void onCoreRemoved(
            ServerLevel level,
            UUID coreUuid
    ) {
        StructureDataManager data = StructureDataManager.get(level);

        // Find the structure associated with the removed core
        StructureInstance instance =
                data.getStructure(coreUuid);

        if (instance == null) {
            return;
        }

        // Preserve the old position before delinking the structure
        BlockPos oldPosition = instance.position();

        // Mark the destroyed structure as inactive
        data.delinkStructureByUUID(coreUuid);

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
            MeteorSpawner.spawn(
                    level,
                    definition,
                    newPos
            );
        });
    }
}