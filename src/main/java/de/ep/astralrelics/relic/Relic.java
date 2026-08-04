package de.ep.astralrelics.relic;

import net.minecraft.server.level.ServerPlayer;

public abstract class Relic {

    // Basic relic information
    private final RelicType type;
    private final String id;
    private final String name;


    // Active ability cooldown in ticks
    // 20 ticks = 1 second
    private final int activeCooldown;


    public Relic(
            RelicType type,
            String id,
            String name,
            int activeCooldown
    ) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.activeCooldown = activeCooldown;
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


    /**
     * Called every server tick (20 times per second).
     *
     * Use only for mechanics that need constant checking.
     *
     * Examples:
     * - Double jump detection
     * - Active ability states
     * - Timers
     * - Movement checks
     */
    public void tick(ServerPlayer player) {

    }


    /**
     * Called periodically for passive effects.
     *
     * Examples:
     * - Speed bonus
     * - Fire immunity
     * - Water bonuses
     * - Damage resistance
     */
    public void applyPassive(ServerPlayer player) {

    }


    /**
     * Called when the player activates the relic ability.
     *
     * Examples:
     * - Echo Jump
     * - Sonic Dash
     * - Phoenix Burst
     */
    public abstract void activate(ServerPlayer player);

}