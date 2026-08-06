package de.ep.astralrelics.relic;


import de.ep.astralrelics.relic.relics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelicRegistry {

    private static final Map<RelicType, Relic> BY_TYPE = new HashMap<>();
    private static final Map<String, Relic> BY_ASTRAL_ID = new HashMap<>();


    public static void init() {
        register(new AbyssRelic());
        register(new BersekerRelic());
        register(new ChronoRelic());
        register(new EchoRelic());
        register(new FrostRelic());
        register(new GaleRelic());
        register(new GravityRelic());
        register(new MagnetRelic());
        register(new MirrorRelic());
        register(new NatureRelic());
        register(new PhoenixRelic());
        register(new ShadowRelic());

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