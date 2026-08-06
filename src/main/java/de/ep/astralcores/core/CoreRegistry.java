package de.ep.astralcores.core;

import de.ep.astralcores.core.cores.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoreRegistry {

    private static final Map<CoreType, Core> BY_TYPE = new HashMap<>();
    private static final Map<String, Core> BY_CORE_ID = new HashMap<>();

    public static void init() {
        register(new AbyssCore());
        register(new BersekerCore());
        register(new ChronoCore());
        register(new EchoCore());
        register(new FrostCore());
        register(new GaleCore());
        register(new GravityCore());
        register(new MagnetCore());
        register(new MirrorCore());
        register(new NatureCore());
        register(new PhoenixCore());
        register(new ShadowCore());
    }

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

    public static Optional<Core> get(CoreType type) {
        return Optional.ofNullable(
                BY_TYPE.get(type)
        );
    }

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

    public static Map<String, Core> getAll() {
        return Map.copyOf(
                BY_CORE_ID
        );
    }
}
