package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.List;

// Defines the static data required to generate and manage a structure
public record StructureDefinition(
        StructureType type,
        BlockPos coreOffset,
        List<Identifier> allowedDimensions,
        List<Identifier> allowedBiomes
) {

    // Returns the resource identifier of the structure NBT template
    public Identifier structureId() {
        return Identifier.fromNamespaceAndPath(
                "astral_cores",
                type.name().toLowerCase()
        );
    }
}