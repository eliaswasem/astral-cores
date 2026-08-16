package de.ep.astralcores.structure;

import de.ep.astralcores.core.CoreType;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public final class CoreToStructureLookup {

    private static final Map<CoreType, Identifier> BY_CORE = Map.ofEntries(

            Map.entry(CoreType.AERO_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.GALE_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.CHRONO_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.FROST_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "frost_shrine")),

            Map.entry(CoreType.PHOENIX_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "phoenix_meteor")),

            Map.entry(CoreType.LEVIATHAN_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.SHADOW_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "shadow_temple")),

            Map.entry(CoreType.BERSERKER_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.ILLUSION_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.NATURE_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", "")),

            Map.entry(CoreType.MAGNET_CORE,
                    Identifier.fromNamespaceAndPath("astralcores", ""))
    );

    public static Optional<Identifier> get(CoreType coreType) {
        return Optional.ofNullable(BY_CORE.get(coreType));
    }
}