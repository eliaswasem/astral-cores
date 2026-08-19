package de.ep.astralcores.structure;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.UUID;
import java.util.Optional;

// Manages the lifecycle, generation, and replacement of all world structures
public final class StructureManager {

    // Checks and populates structures across the world on server startup
    public static void serverStart(ServerLevel level) {
        StructureDataManager data = StructureDataManager.get(level);
        int amount = ConfigManager.get().general.structures_per_core;

        // Resolves the current world dimension identifier
        Identifier currentDimension = level.dimension().identifier();

        for (StructureType type : StructureType.values()) {
            StructureDefinition definition = StructureRegistry.get(type);
            if (definition == null) {
                continue;
            }

            // Checks if the structure is allowed to spawn in this dimension
            if (!definition.allowedDimensions().isEmpty() && !definition.allowedDimensions().contains(currentDimension)) {
                continue;
            }

            long existing = data.countActiveStructures(type);

            // Spawn missing structures until the target amount is reached
            while (existing < amount) {
                Optional<BlockPos> positionOpt = findValidPosition(level, definition, data);

                // Stop spawning if no valid coordinates within configured parameters could be found
                if (positionOpt.isEmpty()) {
                    break;
                }

                BlockPos position = positionOpt.get();

                CoreType coreType = CoreToStructureLookup
                        .getCoreType(type)
                        .orElseThrow(() -> new IllegalStateException("Missing core type mapping for structure: " + type));

                // Resolves the required Core object from the core registry
                Core core = CoreRegistry.get(coreType)
                        .orElseThrow(() -> new IllegalStateException("Failed to load core instance for type: " + coreType));

                StructureSpawner.spawn(level, type, position, core);
                existing++;
            }
        }
    }

    // Attempts to find a valid location matching biome configuration limits and minimum distance rules
    private static Optional<BlockPos> findValidPosition(ServerLevel level, StructureDefinition definition, StructureDataManager data) {
        int radius = ConfigManager.get().general.structure_spawn_radius;
        double minDistanceSq = 200.0 * 200.0;

        // Accesses the shared world spawn position via respawn coordinates data properties
        BlockPos spawnPos = level.getRespawnData().pos();

        for (int attempts = 0; attempts < 1000; attempts++) {
            // Uses safe random getter method to avoid core visibility constraints
            int x = spawnPos.getX() + level.getRandom().nextInt(radius * 2) - radius;
            int z = spawnPos.getZ() + level.getRandom().nextInt(radius * 2) - radius;

            int y = level.getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING,
                    new BlockPos(x, 0, z)
            ).getY();

            BlockPos candidate = new BlockPos(x, y, z);

            // Verifies the candidate position maintains a 200 block clearance from all existing active structures
            boolean tooClose = data.getStructures().values().stream()
                    .filter(StructureDataManager.StructureInstance::active)
                    .anyMatch(instance -> instance.position().distSqr(candidate) < minDistanceSq);

            if (tooClose) {
                continue;
            }

            // Unwraps the world biome registry location identifier key safely
            Identifier currentBiome = level.getBiome(candidate).unwrapKey()
                    .map(key -> key.identifier())
                    .orElse(null);

            // Validates coordinates against the configuration allowed list restrictions
            if (definition.allowedBiomes().isEmpty() || (currentBiome != null && definition.allowedBiomes().contains(currentBiome))) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    // Triggered when a core entity is removed or destroyed to handle its replacement
    public static void onCoreRemoved(ServerLevel level, UUID uuid) {
        StructureDataManager data = StructureDataManager.get(level);
        StructureDataManager.StructureInstance instance = data.getStructure(uuid);

        if (instance == null) {
            return;
        }

        // Deactivate old data entry
        data.deactivateStructureByUUID(uuid);

        StructureDefinition definition = StructureRegistry.get(instance.type());
        if (definition == null) {
            return;
        }

        // Spawns a replacement structure elsewhere following the environmental filters and distance spacing
        findValidPosition(level, definition, data).ifPresent(newPos -> {
            Core core = CoreRegistry.get(instance.coreType())
                    .orElseThrow(() -> new IllegalStateException("Failed to load core instance for type: " + instance.coreType()));

            StructureSpawner.spawn(
                    level,
                    instance.type(),
                    newPos,
                    core
            );
        });
    }
}
