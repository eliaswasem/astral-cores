package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;

import java.util.UUID;

// Defines the data format of one structure instance
public record StructureInstance(
        UUID coreUuid,
        StructureType type,
        BlockPos position,
        boolean hasLinkedCore
) {}