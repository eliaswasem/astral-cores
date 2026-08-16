package de.ep.astralcores.structure;

import de.ep.astralcores.core.CoreType;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public final class CoreToStructureLookup {

    private static final Map<CoreType, Identifier> BY_CORE = Map.of(
            CoreType.PHOENIX_CORE,
            Identifier.fromNamespaceAndPath("astralcores", "phoenix_meteor"),

            CoreType.FROST_CORE,
            Identifier.fromNamespaceAndPath("astralcores", "frost_shrine"),

            CoreType.SHADOW_CORE,
            Identifier.fromNamespaceAndPath("astralcores", "shadow_temple")
    );

    public static Optional<Identifier> get(CoreType coreType) {
        return Optional.ofNullable(BY_CORE.get(coreType));
    }
}