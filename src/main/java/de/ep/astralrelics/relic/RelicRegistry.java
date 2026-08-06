package de.ep.astralrelics.relic;


import de.ep.astralrelics.relic.relics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelicRegistry {

    private static final Map<RelicType, Relic> BY_TYPE = new HashMap<>();
    private static final Map<String, Relic> BY_ASTRAL_ID = new HashMap<>();


    public static void init() {
        register(new AbyssCore());
        register(new BersekerCore());
        register(new ChronoCore());
        register(new EchoCore());
        register(new FrostRelikt());
        register(new GaleCore());
        register(new GravityShard());
        register(new MagnetCore());
        register(new MirrorShard());
        register(new NatureCore());
        register(new PhoenixCore());
        register(new ShadowCore());

    }


    private static void register(Relic relic) {

        BY_TYPE.put(
                relic.getType(),
                relic
        );

        BY_ASTRAL_ID.put(
                relic.getAstralId(),
                relic
        );
    }


    public static Optional<Relic> get(RelicType type) {

        return Optional.ofNullable(
                BY_TYPE.get(type)
        );
    }


    public static Optional<Relic> getByAstralId(String astralId) {

        if (astralId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                BY_ASTRAL_ID.get(
                        astralId.toLowerCase()
                )
        );
    }


    public static Map<String, Relic> getAll() {

        return Map.copyOf(
                BY_ASTRAL_ID
        );
    }
}