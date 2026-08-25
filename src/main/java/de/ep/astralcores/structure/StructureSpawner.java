package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

// Handles structure template placement
public final class StructureSpawner {

    // Places the registered NBT structure
    public static StructureSpawnResult spawn(
            ServerLevel level,
            StructureDefinition definition,
            BlockPos origin
    ) {

        // Resolves the NBT template identifier from the structure definition
        Identifier structureId =
                definition.structureId();

        // Gets Minecraft's structure template manager for the current world
        StructureTemplateManager structureManager =
                level.getStructureManager();

        // Attempts to load the NBT structure template
        Optional<StructureTemplate> optionalTemplate =
                structureManager.get(structureId);

        // Fails explicitly if the registered NBT template cannot be found
        if (optionalTemplate.isEmpty()) {
            throw new IllegalStateException(
                    "Structure template not found: " + structureId
            );
        }

        StructureTemplate template =
                optionalTemplate.get();

        // Creates the placement settings used when inserting the template
        StructurePlaceSettings settings =
                new StructurePlaceSettings()
                        .setKnownShape(true);

        // Places the NBT structure at the calculated world origin
        boolean placed = template.placeInWorld(
                level,
                origin,
                origin,
                settings,
                level.getRandom(),
                2
        );

        // Prevents the structure from being registered if placement failed
        if (!placed) {
            throw new IllegalStateException(
                    "Failed to place structure: "
                            + definition.type()
            );
        }

        // Returns information about the generated structure
        return new StructureSpawnResult(
                definition.type(),
                origin
        );
    }
}