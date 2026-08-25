package de.ep.astralcores.structure.spawners;

import de.ep.astralcores.core.*;
import de.ep.astralcores.structure.CoreToStructureLookup;
import de.ep.astralcores.structure.StructureDataManager;
import de.ep.astralcores.structure.StructureDefinition;
import de.ep.astralcores.structure.StructureSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Optional;

// Handles complete meteor generation including its associated core
public final class MeteorSpawner {

    // Spawns the meteor structure and its associated core
    public static StructureSpawner.StructureSpawnResult spawn(
            ServerLevel level,
            StructureDefinition definition,
            BlockPos origin
    ) {

        // Places the meteor structure itself
        StructureSpawner.StructureSpawnResult result =
                StructureSpawner.spawn(
                        level,
                        definition,
                        origin
                );

        // Resolves the core type associated with this meteor
        Optional<CoreType> coreType =
                CoreToStructureLookup.getCoreType(
                        definition.type()
                );

        // This meteor does not have an associated core
        if (coreType.isEmpty()) {
            return result;
        }

        // Resolves the actual core instance
        Optional<Core> core =
                CoreRegistry.get(coreType.get());

        // Fails explicitly if the configured core cannot be loaded
        if (core.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to load core instance for type: "
                            + coreType.get()
            );
        }

        // Calculates the position where the core item should be spawned
        BlockPos corePosition =
                origin.offset(definition.coreOffset());

        // Creates the item stack representing the core
        CoreStackResult coreStack =
                CoreFactory.createStack(core.get());

        // Creates the floating core item entity
        ItemEntity itemEntity = new ItemEntity(
                level,
                corePosition.getX() + 0.5D,
                corePosition.getY() + 0.5D,
                corePosition.getZ() + 0.5D,
                coreStack.stack()
        );

        // Adds the core entity to the world
        level.addFreshEntity(itemEntity);

        // Registers the generated meteor structure
        StructureDataManager.get(level).addStructure(
                coreStack.uuid(),
                definition.type(),
                origin
        );

        return result;
    }
}