package de.ep.astralcores.core;

import de.ep.astralcores.core.cores.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoreRegistry {

    // Maps core instances by their core type enum identifier
    private static final Map<CoreType, Core> BY_TYPE = new HashMap<>();

    // Maps core instances by their string id key
    private static final Map<String, Core> BY_CORE_ID = new HashMap<>();

    // Initializes and registers all custom core modules into memory maps
    public static void init() {
        register(new LeviathanCore());
        register(new BerserkerCore());
        register(new ChronoCore());
        register(new AeroCore());
        register(new FrostCore());
        register(new GaleCore());
        register(new GravityCore());
        register(new MagnetCore());
        register(new IllusionCore());
        register(new NatureCore());
        register(new PhoenixCore());
        register(new ShadowCore());
    }

    // Helper method to bind a core to both lookup maps
    private static void register(Core core) {
        BY_TYPE.put(
                core.getType(),
                core
        );

        BY_CORE_ID.put(
                core.getCoreId(),
                core
        );
    }

    // Gets a core instance matching the specified core type enum
    public static Optional<Core> get(CoreType type) {
        return Optional.ofNullable(
                BY_TYPE.get(type)
        );
    }

    // Gets a core instance matching the specified string core id
    public static Optional<Core> getByCoreId(String coreID) {
        if (coreID == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                BY_CORE_ID.get(
                        coreID.toLowerCase()
                )
        );
    }

    // Returns an unmodifiable copy of all registered core instances
    public static Map<String, Core> getAll() {
        return Map.copyOf(
                BY_CORE_ID
        );
    }
}
