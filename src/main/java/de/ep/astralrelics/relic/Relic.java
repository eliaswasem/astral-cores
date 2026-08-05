package de.ep.astralrelics.relic;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public abstract class Relic {


    private final RelicType type;

    private final String id;
    private final String name;

    private final int activeCooldown;
    private final int passiveCooldown;


    public Relic(
            RelicType type,
            String id,
            String name,
            int activeCooldown,
            int passiveCooldown
    ) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.activeCooldown = activeCooldown;
        this.passiveCooldown = passiveCooldown;
    }


    public RelicType getType() {
        return type;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public int getActiveCooldown() {
        return activeCooldown;
    }


    public int getPassiveCooldown() {
        return passiveCooldown;
    }


    public void tick(ServerPlayer player) {

    }


    public void applyPassive(ServerPlayer player) {

    }


    public abstract void activate(ServerPlayer player);

}