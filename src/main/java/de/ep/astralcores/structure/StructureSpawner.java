package de.ep.astralcores.structure;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreStackResult;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

// Handles structure template placement and core entity deployment
public final class StructureSpawner {

    private StructureSpawner() {
        // Prevents instantiation of this utility class
    }

    // Places the registered NBT structure and spawns its corresponding core item
    public static StructureSpawnResult spawn(
            ServerLevel level,
            StructureType type,
            BlockPos origin,
            Core core
    ) {

        // Resolves the structure definition registered for this structure type
        StructureDefinition definition = StructureRegistry.get(type);

        if (definition == null) {
            throw new IllegalStateException(
                    "No structure definition registered for: " + type
            );
        }

        // Resolves the NBT template identifier from the structure definition
        Identifier structureId = definition.structureId();

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

        StructureTemplate template = optionalTemplate.get();

        // Creates the placement settings used when inserting the template
        StructurePlaceSettings settings =
                new StructurePlaceSettings();

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
                    "Failed to place structure: " + type
            );
        }

        // Calculates the position where the core item should be spawned
        BlockPos corePosition =
                origin.offset(definition.coreOffset());

        // Creates the item stack representing the core
        CoreStackResult coreStack =
                CoreFactory.createStack(core);

        // Creates the floating core item entity centered on the target block
        ItemEntity itemEntity = new ItemEntity(
                level,
                corePosition.getX() + 0.5D,
                corePosition.getY() + 0.5D,
                corePosition.getZ() + 0.5D,
                coreStack.stack()
        );

        // Adds the core entity to the world
        level.addFreshEntity(itemEntity);

        // Registers the generated structure for persistence and distance tracking
        StructureDataManager.get(level).addStructure(
                itemEntity.getUUID(),
                type,
                origin
        );

        // Returns information about the generated core
        return new StructureSpawnResult(
                coreStack,
                corePosition
        );
    }

    // Contains the results generated during structure spawning
    public record StructureSpawnResult(
            CoreStackResult coreStack,
            BlockPos corePosition
    ) {
    }
}