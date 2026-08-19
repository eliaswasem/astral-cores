package de.ep.astralcores.structure;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;
import java.util.UUID;

public final class StructureManager {

    public static void serverStart(ServerLevel level) {
        StructureDataManager data = StructureDataManager.get(level);
        int amount = ConfigManager.get().general.structures_per_core;
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

            long existing = data.countActiveStructures(type);

            while (existing < amount) {
                Optional<BlockPos> positionOpt = findValidPosition(
                        level,
                        definition,
                        data,
                        type,
                        random,
                        null
                );

                if (positionOpt.isEmpty()) {
                    break;
                }

                BlockPos position = positionOpt.get();

                CoreType coreType = CoreToStructureLookup
                        .getCoreType(type)
                        .orElseThrow(() -> new IllegalStateException(
                                "Missing core type mapping for structure: " + type
                        ));

                Core core = CoreRegistry.get(coreType)
                        .orElseThrow(() -> new IllegalStateException(
                                "Failed to load core instance for type: " + coreType
                        ));

                StructureSpawner.spawn(level, type, position, core);
                existing++;
            }
        }
    }

    private static Optional<BlockPos> findValidPosition(
            ServerLevel level,
            StructureDefinition definition,
            StructureDataManager data,
            StructureType type,
            RandomSource random,
            BlockPos excludedPosition
    ) {
        int radius = ConfigManager.get().general.structure_spawn_radius;
        double minDistanceSq = 200.0 * 200.0;
        BlockPos spawnPos = level.getRespawnData().pos();

        for (int attempts = 0; attempts < 1000; attempts++) {
            int x = spawnPos.getX()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            int z = spawnPos.getZ()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            BlockPos biomePos = new BlockPos(x, level.getMinY(), z);

            Identifier currentBiome = level.getBiome(biomePos)
                    .unwrapKey()
                    .map(key -> key.identifier())
                    .orElse(null);

            if (!definition.allowedBiomes().isEmpty()
                    && (currentBiome == null
                    || !definition.allowedBiomes().contains(currentBiome))) {
                continue;
            }

            if (excludedPosition != null
                    && excludedPosition.distSqr(biomePos) < minDistanceSq) {
                continue;
            }

            boolean tooClose = data.getStructures().values().stream()
                    .filter(StructureDataManager.StructureInstance::active)
                    .anyMatch(instance ->
                            instance.position().distSqr(biomePos) < minDistanceSq
                    );

            if (tooClose) {
                continue;
            }

            int chunkX = x >> 4;
            int chunkZ = z >> 4;

            level.getChunk(chunkX, chunkZ);

            int y = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x,
                    z
            );

            BlockPos candidate = new BlockPos(x, y, z);

            AstralCores.LOGGER.info(
                    "Structure candidate: type={}, x={}, y={}, z={}",
                    type,
                    x,
                    y,
                    z
            );

            return Optional.of(candidate);
        }

        return Optional.empty();
    }

    private static RandomSource createInitialRandom(ServerLevel level) {
        if (ConfigManager.get().general.randomized_structure_spawn) {
            return level.getRandom();
        }

        return RandomSource.create(level.getSeed());
    }

    public static void onCoreRemoved(ServerLevel level, UUID uuid) {
        StructureDataManager data = StructureDataManager.get(level);
        StructureDataManager.StructureInstance instance = data.getStructure(uuid);

        if (instance == null) {
            return;
        }

        BlockPos oldPosition = instance.position();

        data.deactivateStructureByUUID(uuid);

        StructureDefinition definition = StructureRegistry.get(instance.type());

        if (definition == null) {
            return;
        }

        RandomSource random = level.getRandom();

        findValidPosition(
                level,
                definition,
                data,
                instance.type(),
                random,
                oldPosition
        ).ifPresent(newPos -> {
            Core core = CoreRegistry.get(instance.coreType())
                    .orElseThrow(() -> new IllegalStateException(
                            "Failed to load core instance for type: " + instance.coreType()
                    ));

            StructureSpawner.spawn(
                    level,
                    instance.type(),
                    newPos,
                    core
            );
        });
    }
}