package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

// Handles raw structure template placement.
public final class StructurePlacer {

    private StructurePlacer() {
    }

    // Creates the placement settings used for structure placement.
    public static StructurePlaceSettings createPlacementSettings() {
        return new StructurePlaceSettings()
                .setKnownShape(true);
    }

    // Calculates the world-space bounding box used by the structure placement.
    public static BoundingBox getBoundingBox(
            StructureTemplate template,
            BlockPos origin
    ) {
        return template.getBoundingBox(
                createPlacementSettings(),
                origin
        );
    }

    // Places the supplied structure template at the given world position.
    public static StructureSpawnResult place(
            ServerLevel level,
            StructureTemplate template,
            StructureType type,
            BlockPos origin
    ) {
        // Creates the same placement settings used when calculating the structure bounding box.
        StructurePlaceSettings settings =
                createPlacementSettings();

        // Places the structure template in the world.
        boolean placed = template.placeInWorld(
                level,
                origin,
                origin,
                settings,
                level.getRandom(),
                2
        );

        // Fails explicitly if placement failed.
        if (!placed) {
            throw new IllegalStateException(
                    "Failed to place structure: " + type
            );
        }

        // The structure-specific spawner supplies the core UUID.
        return new StructureSpawnResult(
                type,
                origin,
                null
        );
    }
}