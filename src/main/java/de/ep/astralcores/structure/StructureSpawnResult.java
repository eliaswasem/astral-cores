        package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;

import java.util.UUID;

// Contains the results generated during structure spawning.
public record StructureSpawnResult(
        StructureType type,
        BlockPos origin,
        UUID coreUuid
) {
}
