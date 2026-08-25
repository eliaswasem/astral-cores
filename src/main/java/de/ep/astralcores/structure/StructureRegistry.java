package de.ep.astralcores.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Stores the static parameters and metrics for all available structures
public final class StructureRegistry {

    private static final Map<StructureType, StructureDefinition> STRUCTURES = Map.ofEntries(
            Map.entry(StructureType.AERO_CORE_METEOR, new StructureDefinition(
                    StructureType.AERO_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "plains"), Identifier.fromNamespaceAndPath("minecraft", "desert"))
            )),
            Map.entry(StructureType.GALE_CORE_METEOR, new StructureDefinition(
                    StructureType.GALE_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "desert"))
            )),
            Map.entry(StructureType.CHRONO_CORE_METEOR, new StructureDefinition(
                    StructureType.CHRONO_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of()
            )),
            Map.entry(StructureType.GRAVITY_CORE_METEOR, new StructureDefinition(
                    StructureType.GRAVITY_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "the_nether")),
                    List.of()
            )),
            Map.entry(StructureType.FROST_CORE_METEOR, new StructureDefinition(
                    StructureType.FROST_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "snowy_plains"), Identifier.fromNamespaceAndPath("minecraft", "ice_spikes"))
            )),
            Map.entry(StructureType.PHOENIX_CORE_METEOR, new StructureDefinition(
                    StructureType.PHOENIX_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "the_nether")),
                    List.of()
            )),
            Map.entry(StructureType.LEVIATHAN_CORE_METEOR, new StructureDefinition(
                    StructureType.LEVIATHAN_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "ocean"), Identifier.fromNamespaceAndPath("minecraft", "deep_ocean"))
            )),
            Map.entry(StructureType.SHADOW_CORE_METEOR, new StructureDefinition(
                    StructureType.SHADOW_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "the_end")),
                    List.of()
            )),
            Map.entry(StructureType.BERSERKER_CORE_METEOR, new StructureDefinition(
                    StructureType.BERSERKER_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of()
            )),
            Map.entry(StructureType.ILLUSION_CORE_METEOR, new StructureDefinition(
                    StructureType.ILLUSION_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of()
            )),
            Map.entry(StructureType.NATURE_CORE_METEOR, new StructureDefinition(
                    StructureType.NATURE_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "forest"), Identifier.fromNamespaceAndPath("minecraft", "dark_forest"))
            )),
            Map.entry(StructureType.MAGNET_CORE_METEOR, new StructureDefinition(
                    StructureType.MAGNET_CORE_METEOR,
                    new BlockPos(2, 1, 2),
                    List.of(Identifier.fromNamespaceAndPath("minecraft", "overworld")),
                    List.of()
            ))
    );


    public static StructureDefinition get(StructureType type) {
        return STRUCTURES.get(type);
    }

    public static Collection<String> getAll() {
        return STRUCTURES.values().stream()
                .map(definition -> definition.structureId().toString())
                .toList();
    }

    public static Optional<StructureType> getByStructureType(Identifier structureType) {
        return STRUCTURES.values().stream()
                .map(StructureDefinition::type)
                .filter(type -> type.name().equals(structureType.getPath().toUpperCase()))
                .findFirst();
    }
}
