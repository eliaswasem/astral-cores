package de.ep.astralcores.structure;

import de.ep.astralcores.AstralCores;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

// Handles structure template loading and template-related calculations.
public final class TemplateManager {

    private TemplateManager() {
    }

    // Loads the NBT structure template registered by the structure definition.
    public static Optional<StructureTemplate> get(
            ServerLevel level,
            StructureDefinition definition
    ) {
        Identifier structureId =
                definition.structureId();

        Optional<StructureTemplate> template =
                level.getStructureManager().get(structureId);

        if (template.isEmpty()) {
            AstralCores.LOGGER.error(
                    "Structure template not found: {}",
                    structureId
            );
        }

        return template;
    }

    // Creates the placement settings used by structure placement and bounding-box calculation.
    public static StructurePlaceSettings createPlacementSettings() {
        return new StructurePlaceSettings()
                .setKnownShape(true);
    }

    // Calculates the world-space bounding box of a structure at the given origin.
    public static BoundingBox getBoundingBox(
            StructureTemplate template,
            BlockPos origin
    ) {
        return template.getBoundingBox(
                createPlacementSettings(),
                origin
        );
    }
}