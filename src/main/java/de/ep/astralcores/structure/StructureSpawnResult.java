package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;

// Contains the results generated during structure spawning
public record StructureSpawnResult(
        StructureType type,
        BlockPos origin
) {}