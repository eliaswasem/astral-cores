package de.ep.astralcores.util;

public final class TickTimer {

    private int ticks;

    public TickTimer(int ticks) {
        this.ticks = ticks;
    }

    public boolean tick() {
        return --ticks <= 0;
    }

    public int getRemaining() {
        return ticks;
    }
}