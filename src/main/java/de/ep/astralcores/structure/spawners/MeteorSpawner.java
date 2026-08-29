package de.ep.astralcores.structure.spawners;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreStackResult;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.structure.CoreToStructureLookup;
import de.ep.astralcores.structure.StructureDefinition;
import de.ep.astralcores.structure.StructurePlacer;
import de.ep.astralcores.structure.StructureSpawnResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

// Handles complete meteor generation including its associated core.
public final class MeteorSpawner {

    // Spawns the meteor structure and its associated core.
    public static StructureSpawnResult spawn(
            ServerLevel level,
            StructureDefinition definition,
            BlockPos origin,
            StructureTemplate template
    ) {
        // Places the meteor structure itself.
        StructureSpawnResult result =
                StructurePlacer.place(
                        level,
                        template,
                        definition.type(),
                        origin
                );

        // Resolves the core type associated with this meteor.
        Optional<CoreType> coreType =
                CoreToStructureLookup.getCoreType(
                        definition.type()
                );

        // This meteor does not have an associated core.
        if (coreType.isEmpty()) {
            return result;
        }

        // Resolves the actual core instance.
        Optional<Core> core =
                CoreRegistry.get(
                        coreType.get()
                );

        // Fails explicitly if the configured core cannot be loaded.
        if (core.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to load core instance for type: "
                            + coreType.get()
            );
        }

        // Calculates the position where the core item should be spawned.
        BlockPos corePosition =
                origin.offset(
                        definition.coreOffset()
                );

        // Creates the item stack representing the core.
        CoreStackResult coreStack =
                CoreFactory.createStack(
                        core.get()
                );

        // Creates the floating core item entity.
        ItemEntity itemEntity =
                new ItemEntity(
                        level,
                        corePosition.getX() + 0.5D,
                        corePosition.getY() + 0.5D,
                        corePosition.getZ() + 0.5D,
                        coreStack.stack()
                );

        // Adds the core entity to the world.
        level.addFreshEntity(itemEntity);

        // The core UUID is also the structure UUID.
        return new StructureSpawnResult(
                result.type(),
                result.origin(),
                coreStack.uuid()
        );
    }
}