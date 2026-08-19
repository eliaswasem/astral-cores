package de.ep.astralcores.structure;

import de.ep.astralcores.core.CoreType;

import java.util.Map;
import java.util.Optional;

// Provides the mapping between core types and their associated structures
public final class CoreToStructureLookup {


    // Stores the relation between every core type and structure type
    private static final Map<CoreType, StructureType> BY_CORE = Map.ofEntries(

            Map.entry(
                    CoreType.AERO_CORE,
                    StructureType.AERO_CORE_METEOR
            ),

            Map.entry(
                    CoreType.GALE_CORE,
                    StructureType.GALE_CORE_METEOR
            ),

            Map.entry(
                    CoreType.CHRONO_CORE,
                    StructureType.CHRONO_CORE_METEOR
            ),

            Map.entry(
                    CoreType.GRAVITY_CORE,
                    StructureType.GRAVITY_CORE_METEOR
            ),

            Map.entry(
                    CoreType.FROST_CORE,
                    StructureType.FROST_CORE_METEOR
            ),

            Map.entry(
                    CoreType.PHOENIX_CORE,
                    StructureType.PHOENIX_CORE_METEOR
            ),

            Map.entry(
                    CoreType.LEVIATHAN_CORE,
                    StructureType.LEVIATHAN_CORE_METEOR
            ),

            Map.entry(
                    CoreType.SHADOW_CORE,
                    StructureType.SHADOW_CORE_METEOR
            ),

            Map.entry(
                    CoreType.BERSERKER_CORE,
                    StructureType.BERSERKER_CORE_METEOR
            ),

            Map.entry(
                    CoreType.ILLUSION_CORE,
                    StructureType.ILLUSION_CORE_METEOR
            ),

            Map.entry(
                    CoreType.NATURE_CORE,
                    StructureType.NATURE_CORE_METEOR
            ),

            Map.entry(
                    CoreType.MAGNET_CORE,
                    StructureType.MAGNET_CORE_METEOR
            )
    );


    // Gets the structure associated with a core type
    public static Optional<StructureType> get(
            CoreType coreType
    ) {

        return Optional.ofNullable(
                BY_CORE.get(coreType)
        );
    }


    // Gets the core type associated with a structure type
    public static Optional<CoreType> getCoreType(
            StructureType structureType
    ) {

        return BY_CORE.entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                == structureType
                )
                .map(Map.Entry::getKey)
                .findFirst();
    }


    // Checks if a core type has a registered structure
    public static boolean contains(
            CoreType coreType
    ) {

        return BY_CORE.containsKey(
                coreType
        );
    }


    // Checks if a structure type has a registered core
    public static boolean contains(
            StructureType structureType
    ) {

        return BY_CORE.containsValue(
                structureType
        );
    }


}